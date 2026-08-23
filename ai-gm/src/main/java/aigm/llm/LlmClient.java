package aigm.llm;

/**
 * One chat completion. Implement this to attach any model runtime.
 * <p>
 * Implementations must not retry internally. Throw {@link LlmException} with
 * {@link LlmException#retryable()} set so Temporal activities can retry.
 */
public interface LlmClient {

    LlmResponse complete(LlmRequest request);

    /** Short label for logs (class + model/host), never include secrets. */
    default String describe() {
        return getClass().getSimpleName();
    }
}
