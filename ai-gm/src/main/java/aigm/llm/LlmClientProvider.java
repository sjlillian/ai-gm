package aigm.llm;

/**
 * SPI for discovering LLM backends. Register implementations in
 * {@code META-INF/services/aigm.llm.LlmClientProvider} and select them with
 * {@code AIGM_LLM_CLIENT=<id>}.
 */
public interface LlmClientProvider {

    /** Stable id, e.g. {@code openai} or {@code stub}. Compared case-insensitively. */
    String id();

    LlmClient create(LlmSettings settings);
}
