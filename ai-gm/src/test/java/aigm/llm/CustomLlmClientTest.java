package aigm.llm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Shows the SPI a table can implement for a non-OpenAI local runtime. */
class CustomLlmClientTest {

    @Test
    void classNameLoadsCustomClient() {
        LlmSettings settings = LlmSettings.from(key -> null);
        LlmClient client = LlmClients.create(FixedReplyClient.class.getName(), settings);
        LlmResponse response = client.complete(LlmRequest.builder().addUser("ping").build());
        assertEquals("pong", response.content());
        assertEquals("fixed", response.model());
    }

    public static final class FixedReplyClient implements LlmClient {
        @SuppressWarnings("unused")
        public FixedReplyClient(LlmSettings settings) {}

        @Override
        public LlmResponse complete(LlmRequest request) {
            return new LlmResponse("pong", "fixed", LlmResponse.Usage.NONE, "stop");
        }
    }
}
