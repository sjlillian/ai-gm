package aigm.llm;

import java.util.ArrayList;
import java.util.List;

/**
 * A single chat completion request. {@code model} null means "use the client's default".
 */
public record LlmRequest(
    List<LlmMessage> messages,
    String model,
    Double temperature,
    Integer maxTokens,
    boolean jsonObject
) {

    public LlmRequest {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("messages must not be empty");
        }
        messages = List.copyOf(messages);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<LlmMessage> messages = new ArrayList<>();
        private String model;
        private Double temperature;
        private Integer maxTokens;
        private boolean jsonObject;

        public Builder addSystem(String content) {
            messages.add(LlmMessage.system(content));
            return this;
        }

        public Builder addUser(String content) {
            messages.add(LlmMessage.user(content));
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder jsonObject(boolean jsonObject) {
            this.jsonObject = jsonObject;
            return this;
        }

        public LlmRequest build() {
            return new LlmRequest(messages, model, temperature, maxTokens, jsonObject);
        }
    }
}
