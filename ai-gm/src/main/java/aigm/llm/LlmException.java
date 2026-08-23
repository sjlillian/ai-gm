package aigm.llm;

/**
 * Failure talking to a model. {@link #retryable()} covers timeouts, 429s, and 5xx;
 * auth and validation errors are not retryable.
 */
public class LlmException extends RuntimeException {

    private final boolean retryable;
    private final int statusCode;

    public LlmException(String message, boolean retryable, int statusCode) {
        super(message);
        this.retryable = retryable;
        this.statusCode = statusCode;
    }

    public LlmException(String message, boolean retryable, int statusCode, Throwable cause) {
        super(message, cause);
        this.retryable = retryable;
        this.statusCode = statusCode;
    }

    public boolean retryable() {
        return retryable;
    }

    public int statusCode() {
        return statusCode;
    }

    public static LlmException retryable(String message, int statusCode) {
        return new LlmException(message, true, statusCode);
    }

    public static LlmException retryable(String message, Throwable cause) {
        return new LlmException(message, true, 0, cause);
    }

    public static LlmException fatal(String message, int statusCode) {
        return new LlmException(message, false, statusCode);
    }
}
