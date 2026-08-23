package aigm.llm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JsonTextsTest {

    @Test
    void extractsObjectFromMarkdownFence() {
        String raw = """
            Sure.
            ```json
            {"position":"RISKY","effect":"STANDARD"}
            ```
            """;
        assertEquals("{\"position\":\"RISKY\",\"effect\":\"STANDARD\"}", JsonTexts.extractObject(raw));
    }

    @Test
    void emptyBecomesObject() {
        assertEquals("{}", JsonTexts.extractObject("   "));
    }
}
