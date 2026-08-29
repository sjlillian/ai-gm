package aigm.llm;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OpenAI Chat Completions API ({@code POST .../v1/chat/completions}).
 * Works with OpenAI, Azure-compatible proxies, Groq, OpenRouter, Ollama,
 * LM Studio, llama.cpp server, vLLM, LocalAI, and anything else that
 * speaks that wire format — including models running on this machine.
 * <p>
 * HTTP retries are disabled; Temporal retries the activity instead.
 */
public final class OpenAiCompatibleClient implements LlmClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCompatibleClient.class);
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final LlmSettings settings;
    private final OkHttpClient http;

    public OpenAiCompatibleClient(LlmSettings settings) {
        this(settings, defaultHttp(settings));
    }

    public OpenAiCompatibleClient(LlmSettings settings, OkHttpClient http) {
        this.settings = settings;
        this.http = http;
    }

    static OkHttpClient defaultHttp(LlmSettings settings) {
        long timeoutMs = settings.timeout().toMillis();
        return new OkHttpClient.Builder()
            .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .callTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(false)
            .build();
    }

    @Override
    public LlmResponse complete(LlmRequest request) {
        String url = chatCompletionsUrl(settings.baseUrl());
        String bodyJson;
        try {
            bodyJson = MAPPER.writeValueAsString(toBody(request));
        } catch (IOException e) {
            throw LlmException.fatal("Could not serialize LLM request: " + e.getMessage(), 0);
        }

        Request.Builder builder = new Request.Builder()
            .url(url)
            .post(RequestBody.create(bodyJson, JSON));
        if (settings.hasApiKey()) {
            builder.header("Authorization", "Bearer " + settings.apiKey());
        }
        builder.header("Accept", "application/json");

        try (Response response = http.newCall(builder.build()).execute()) {
            String body = readBody(response);
            if (!response.isSuccessful()) {
                throw fromHttp(response.code(), body);
            }
            return parseCompletion(body);
        } catch (LlmException e) {
            throw e;
        } catch (IOException e) {
            throw LlmException.retryable("LLM HTTP call failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String describe() {
        String kind = settings.looksLikeOllama() ? "Ollama" : "OpenAiCompatibleClient";
        return kind + " " + settings.describe();
    }

    ObjectNode toBody(LlmRequest request) {
        ObjectNode root = MAPPER.createObjectNode();
        String model = request.model() == null || request.model().isBlank()
            ? settings.resolvedModel()
            : request.model();
        root.put("model", model);

        ArrayNode messages = root.putArray("messages");
        for (LlmMessage message : request.messages()) {
            ObjectNode node = messages.addObject();
            node.put("role", message.role().apiName());
            node.put("content", message.content());
        }
        if (request.temperature() != null) {
            root.put("temperature", request.temperature());
        }
        if (request.maxTokens() != null) {
            root.put("max_tokens", request.maxTokens());
        }
        if (request.jsonObject() && settings.jsonMode()) {
            root.putObject("response_format").put("type", "json_object");
        }
        return root;
    }

    static String chatCompletionsUrl(String baseUrl) {
        String base = LlmSettings.normalizeBaseUrl(baseUrl);
        if (base.endsWith("/chat/completions")) {
            return base;
        }
        if (base.endsWith("/v1")) {
            return base + "/chat/completions";
        }
        return base + "/v1/chat/completions";
    }

    private static String readBody(Response response) throws IOException {
        ResponseBody body = response.body();
        return body == null ? "" : body.string();
    }

    private static LlmException fromHttp(int status, String body) {
        String message = errorMessage(body, status);
        log.warn("LLM HTTP {}: {}", status, message);
        if (status == 408 || status == 409 || status == 429 || status >= 500) {
            return LlmException.retryable(message, status);
        }
        return LlmException.fatal(message, status);
    }

    private static String errorMessage(String body, int status) {
        JsonNode node = readTreeQuietly(body);
        if (node != null) {
            JsonNode error = node.get("error");
            if (error != null) {
                if (error.isTextual()) {
                    return "LLM HTTP " + status + ": " + error.asText();
                }
                JsonNode msg = error.get("message");
                if (msg != null && msg.isTextual()) {
                    return "LLM HTTP " + status + ": " + msg.asText();
                }
            }
        }
        String snippet = body == null ? "" : body.replaceAll("\\s+", " ");
        if (snippet.length() > 300) {
            snippet = snippet.substring(0, 300);
        }
        return "LLM HTTP " + status + (snippet.isBlank() ? "" : ": " + snippet);
    }

    private static LlmResponse parseCompletion(String body) {
        JsonNode root = readTreeQuietly(body);
        if (root == null) {
            throw LlmException.retryable("LLM returned non-JSON body", 200);
        }
        JsonNode choice = root.path("choices").path(0);
        String content = textOrEmpty(choice.path("message").path("content"));
        if (content.isBlank()) {
            content = textOrEmpty(choice.path("text"));
        }
        String model = textOrEmpty(root.path("model"));
        String finish = textOrEmpty(choice.path("finish_reason"));
        JsonNode usageNode = root.path("usage");
        LlmResponse.Usage usage = new LlmResponse.Usage(
            usageNode.path("prompt_tokens").asInt(0),
            usageNode.path("completion_tokens").asInt(0),
            usageNode.path("total_tokens").asInt(0)
        );
        return new LlmResponse(content, model, usage, finish);
    }

    private static String textOrEmpty(JsonNode node) {
        return node == null || node.isNull() ? "" : node.asText();
    }

    private static JsonNode readTreeQuietly(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readTree(body);
        } catch (IOException e) {
            return null;
        }
    }
}
