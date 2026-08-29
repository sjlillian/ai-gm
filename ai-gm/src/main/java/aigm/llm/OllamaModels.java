package aigm.llm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/** Ollama native API helpers (model listing). Chat uses {@link OpenAiCompatibleClient}. */
public final class OllamaModels {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private OllamaModels() {}

    public static List<String> list(LlmSettings settings) {
        return list(settings, defaultHttp(settings));
    }

    static List<String> list(LlmSettings settings, OkHttpClient http) {
        String url = tagsUrl(settings.baseUrl());
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = http.newCall(request).execute()) {
            String body = readBody(response);
            if (!response.isSuccessful()) {
                throw LlmException.fatal(
                    "Ollama tags HTTP " + response.code() + ": " + snippet(body),
                    response.code()
                );
            }
            return parseNames(body);
        } catch (LlmException e) {
            throw e;
        } catch (IOException e) {
            throw LlmException.retryable("Could not reach Ollama at " + url + ": " + e.getMessage(), e);
        }
    }

    static String tagsUrl(String baseUrl) {
        return ollamaHost(baseUrl) + "/api/tags";
    }

    static String ollamaHost(String baseUrl) {
        String base = LlmSettings.normalizeBaseUrl(baseUrl);
        if (base.endsWith("/v1")) {
            base = base.substring(0, base.length() - 3);
        }
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base;
    }

    private static List<String> parseNames(String body) {
        JsonNode root;
        try {
            root = MAPPER.readTree(body);
        } catch (IOException e) {
            throw LlmException.fatal("Ollama tags response was not JSON: " + e.getMessage(), 200);
        }
        List<String> names = new ArrayList<>();
        JsonNode models = root.path("models");
        if (models.isArray()) {
            models.forEach(item -> {
                String name = item.path("name").asText("");
                if (!name.isBlank()) {
                    names.add(name);
                }
            });
        }
        return names;
    }

    private static OkHttpClient defaultHttp(LlmSettings settings) {
        long timeoutMs = settings.timeout().toMillis();
        return new OkHttpClient.Builder()
            .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .callTimeout(timeoutMs, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(false)
            .build();
    }

    private static String readBody(Response response) throws IOException {
        ResponseBody body = response.body();
        return body == null ? "" : body.string();
    }

    private static String snippet(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }
        String flat = body.replaceAll("\\s+", " ");
        return flat.length() > 200 ? flat.substring(0, 200) : flat;
    }
}
