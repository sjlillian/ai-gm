package aigm.llm;

public record LlmResponse(String content, String model, Usage usage, String finishReason) {

    public LlmResponse {
        content = content == null ? "" : content;
        usage = usage == null ? Usage.NONE : usage;
        finishReason = finishReason == null ? "" : finishReason;
    }

    public record Usage(int promptTokens, int completionTokens, int totalTokens) {
        public static final Usage NONE = new Usage(0, 0, 0);
    }
}
