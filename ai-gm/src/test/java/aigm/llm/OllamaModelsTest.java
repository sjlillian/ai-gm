package aigm.llm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

class OllamaModelsTest {

    @Test
    void tagsUrlStripsOpenAiPath() {
        assertEquals(
            "http://127.0.0.1:11434/api/tags",
            OllamaModels.tagsUrl("http://127.0.0.1:11434/v1")
        );
        assertEquals(
            "http://127.0.0.1:11434/api/tags",
            OllamaModels.tagsUrl("http://127.0.0.1:11434")
        );
    }

    @Test
    void parsesModelNames() {
        LlmSettings settings = new LlmSettings(
            "ollama",
            "http://127.0.0.1:11434/v1",
            "",
            "",
            Duration.ofSeconds(5),
            false
        );
        String payload = """
            {"models":[{"name":"llama3.1:latest"},{"name":"mistral:7b"}]}
            """;
        OkHttpClient http = new OkHttpClient.Builder()
            .addInterceptor(fixedResponse(200, payload))
            .build();
        List<String> names = OllamaModels.list(settings, http);
        assertEquals(List.of("llama3.1:latest", "mistral:7b"), names);
    }

    @Test
    void ollamaClientDefaultsJsonModeOff() {
        LlmSettings settings = LlmSettings.from(
            key -> "ollama".equals(key) ? "ollama" : null
        );
        assertTrue(settings.looksLikeOllama());
        assertEquals(false, settings.jsonMode());
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
}
