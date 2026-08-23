package aigm.llm;

public record LlmMessage(Role role, String content) {

    public enum Role {
        SYSTEM,
        USER,
        ASSISTANT;

        public String apiName() {
            return name().toLowerCase();
        }
    }

    public LlmMessage {
        if (role == null) {
            throw new IllegalArgumentException("role is required");
        }
        content = content == null ? "" : content;
    }

    public static LlmMessage system(String content) {
        return new LlmMessage(Role.SYSTEM, content);
    }

    public static LlmMessage user(String content) {
        return new LlmMessage(Role.USER, content);
    }
}
