package aigm.llm;

/** Pull a JSON object out of model text that may include markdown fences or prose. */
public final class JsonTexts {

    private JsonTexts() {}

    public static String extractObject(String text) {
        if (text == null || text.isBlank()) {
            return "{}";
        }
        String trimmed = text.trim();
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }
}
