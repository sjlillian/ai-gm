package aigm.llm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import aigm.llm.openai.OpenAiCompatibleClient;
import aigm.llm.stub.StubLlmClient;

class LlmClientsTest {

    @Test
    void noConfigUsesStub() {
        LlmClient client = LlmClients.from(key -> null);
        assertInstanceOf(StubLlmClient.class, client);
    }

    @Test
    void explicitClientId() {
        Map<String, String> env = Map.of(LlmSettings.ENV_CLIENT, "stub");
        assertInstanceOf(StubLlmClient.class, LlmClients.from(env::get));
    }

    @Test
    void modelEnvSelectsOpenAiCompatible() {
        Map<String, String> env = Map.of(
            LlmSettings.ENV_BASE_URL, "http://127.0.0.1:1234/v1",
            LlmSettings.ENV_MODEL, "local-model"
        );
        LlmClient client = LlmClients.from(env::get);
        assertInstanceOf(OpenAiCompatibleClient.class, client);
        assertTrue(client.describe().contains("local-model"));
        assertTrue(client.describe().contains("1234"));
        assertFalse(client.describe().toLowerCase().contains("sk-"));
    }

    @Test
    void settingsPreferDedicatedApiKey() {
        Map<String, String> env = new HashMap<>();
        env.put(LlmSettings.ENV_API_KEY, "aigm-key");
        env.put(LlmSettings.ENV_OPENAI_API_KEY, "openai-key");
        LlmSettings settings = LlmSettings.from(env::get);
        assertEquals("aigm-key", settings.apiKey());
        assertEquals(LlmSettings.DEFAULT_OPENAI_URL, settings.baseUrl());
        assertEquals(Duration.ofSeconds(120), settings.timeout());
    }

    @Test
    void openaiUrlWhenOnlyOpenAiKeyPresent() {
        LlmSettings settings = LlmSettings.from(Map.of(LlmSettings.ENV_OPENAI_API_KEY, "sk-test")::get);
        assertTrue(settings.looksLikeOfficialOpenAi());
        assertEquals("gpt-4o-mini", settings.resolvedModel());
    }
}
