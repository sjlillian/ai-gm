package aigm.llm.openai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import aigm.llm.LlmException;
import aigm.llm.LlmRequest;
import aigm.llm.LlmResponse;
import aigm.llm.LlmSettings;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

class OpenAiCompatibleClientTest {

    @Test
    void joinsChatCompletionsPath() {
        assertEquals(
            "http://127.0.0.1:11434/v1/chat/completions",
            OpenAiCompatibleClient.chatCompletionsUrl("http://127.0.0.1:11434")
        );
        assertEquals(
            "http://127.0.0.1:1234/v1/chat/completions",
            OpenAiCompatibleClient.chatCompletionsUrl("http://127.0.0.1:1234/v1/")
        );
        assertEquals(
            "https://api.openai.com/v1/chat/completions",
            OpenAiCompatibleClient.chatCompletionsUrl("https://api.openai.com/v1")
        );
    }

    @Test
    void requestBodyUsesOpenAiShape() throws Exception {
        LlmSettings settings = new LlmSettings(
            "openai",
            "http://127.0.0.1:11434/v1",
            "",
            "llama3.1",
            Duration.ofSeconds(30),
            true
        );
        OpenAiCompatibleClient client = new OpenAiCompatibleClient(settings, unusedHttp());
        LlmRequest request = LlmRequest.builder()
            .addSystem("sys")
            .addUser("hello")
            .temperature(0.2)
            .maxTokens(64)
            .jsonObject(true)
            .build();
        JsonNode body = client.toBody(request);
        assertEquals("llama3.1", body.get("model").asText());
        assertEquals("system", body.get("messages").get(0).get("role").asText());
        assertEquals("json_object", body.get("response_format").get("type").asText());
        assertEquals(64, body.get("max_tokens").asInt());
    }

    @Test
    void parsesChatCompletion() {
        String payload = """
            {"id":"x","model":"llama3.1","choices":[{"message":{"role":"assistant","content":"The rain hisses."},"finish_reason":"stop"}],"usage":{"prompt_tokens":11,"completion_tokens":4,"total_tokens":15}}
            """;
        OpenAiCompatibleClient client = clientReturning(200, payload);
        LlmResponse response = client.complete(LlmRequest.builder().addUser("go").build());
        assertEquals("The rain hisses.", response.content());
        assertEquals("llama3.1", response.model());
        assertEquals(15, response.usage().totalTokens());
    }

    @Test
    void rateLimitIsRetryable() {
        OpenAiCompatibleClient client = clientReturning(429, "{\"error\":{\"message\":\"slow down\"}}");
        LlmException thrown = assertThrows(
            LlmException.class,
            () -> client.complete(LlmRequest.builder().addUser("go").build())
        );
        assertTrue(thrown.retryable());
        assertEquals(429, thrown.statusCode());
    }

    @Test
    void unauthorizedIsFatal() {
        OpenAiCompatibleClient client = clientReturning(401, "{\"error\":{\"message\":\"bad key\"}}");
        LlmException thrown = assertThrows(
            LlmException.class,
            () -> client.complete(LlmRequest.builder().addUser("go").build())
        );
        assertFalse(thrown.retryable());
        assertEquals(401, thrown.statusCode());
    }

    private static OpenAiCompatibleClient clientReturning(int status, String json) {
        LlmSettings settings = new LlmSettings(
            "openai",
            "http://127.0.0.1:9/v1",
            "none",
            "m",
            Duration.ofSeconds(5),
            true
        );
        OkHttpClient http = new OkHttpClient.Builder()
            .addInterceptor(fixedResponse(status, json))
            .retryOnConnectionFailure(false)
            .build();
        return new OpenAiCompatibleClient(settings, http);
    }

    private static Interceptor fixedResponse(int status, String json) {
        return chain -> new Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(status)
            .message("test")
            .body(ResponseBody.create(json, MediaType.get("application/json")))
            .build();
    }

    private static OkHttpClient unusedHttp() {
        return new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                throw new IOException("HTTP should not be called");
            })
            .build();
    }
}
