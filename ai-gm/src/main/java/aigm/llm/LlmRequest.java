package aigm.llm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A single chat completion request. {@code model} null means "use the client's default".
 * {@code extraBody} is merged into the provider JSON (Ollama {@code keep_alive}, etc.).
 */
public record LlmRequest(
    List<LlmMessage> messages,
    String model,
    Double temperature,
    Integer maxTokens,
    boolean jsonObject,
    Map<String, Object> extraBody
) {

    public LlmRequest {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("messages must not be empty");
        }
        messages = List.copyOf(messages);
        extraBody = extraBody == null ? Map.of() : Map.copyOf(extraBody);
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
        private Map<String, Object> extraBody = Map.of();

        public Builder add(LlmMessage message) {
            messages.add(message);
            return this;
        }

        public Builder addSystem(String content) {
            return add(LlmMessage.system(content));
        }

        public Builder addUser(String content) {
            return add(LlmMessage.user(content));
        }

        public Builder addAssistant(String content) {
            return add(LlmMessage.assistant(content));
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

        public Builder extraBody(Map<String, Object> extraBody) {
            this.extraBody = extraBody;
            return this;
        }

        public LlmRequest build() {
            return new LlmRequest(messages, model, temperature, maxTokens, jsonObject, extraBody);
        }
    }
}
