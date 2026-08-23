package aigm.llm;

import java.time.Duration;
import java.util.Locale;
import java.util.function.Function;

/**
 * Connection settings for an HTTP (or HTTP-like) LLM backend.
 * <p>
 * Environment:
 * <ul>
 *   <li>{@code AIGM_LLM_CLIENT} — provider id ({@code openai}, {@code stub}) or a class name</li>
 *   <li>{@code AIGM_LLM_BASE_URL} — e.g. {@code http://127.0.0.1:11434} (Ollama),
 *       {@code http://127.0.0.1:1234/v1} (LM Studio), {@code https://api.openai.com/v1}</li>
 *   <li>{@code AIGM_LLM_API_KEY} — optional; falls back to {@code OPENAI_API_KEY}</li>
 *   <li>{@code AIGM_LLM_MODEL} — model id the provider expects</li>
 *   <li>{@code AIGM_LLM_TIMEOUT_SECONDS} — HTTP call timeout (default 120)</li>
 *   <li>{@code AIGM_LLM_JSON_MODE} — send OpenAI {@code response_format=json_object} (default true)</li>
 * </ul>
 */
public record LlmSettings(
    String clientId,
    String baseUrl,
    String apiKey,
    String model,
    Duration timeout,
    boolean jsonMode
) {

    public static final String ENV_CLIENT = "AIGM_LLM_CLIENT";
    public static final String ENV_BASE_URL = "AIGM_LLM_BASE_URL";
    public static final String ENV_API_KEY = "AIGM_LLM_API_KEY";
    public static final String ENV_OPENAI_API_KEY = "OPENAI_API_KEY";
    public static final String ENV_MODEL = "AIGM_LLM_MODEL";
    public static final String ENV_TIMEOUT_SECONDS = "AIGM_LLM_TIMEOUT_SECONDS";
    public static final String ENV_JSON_MODE = "AIGM_LLM_JSON_MODE";

    public static final String DEFAULT_OPENAI_URL = "https://api.openai.com/v1";
    public static final String DEFAULT_LOCAL_URL = "http://127.0.0.1:11434/v1";
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(120);

    public LlmSettings {
        clientId = clientId == null || clientId.isBlank() ? "openai" : clientId.trim();
        baseUrl = normalizeBaseUrl(baseUrl);
        apiKey = apiKey == null ? "" : apiKey;
        model = model == null ? "" : model.trim();
        timeout = timeout == null || timeout.isNegative() || timeout.isZero() ? DEFAULT_TIMEOUT : timeout;
    }

    public static LlmSettings fromEnvironment() {
        return from(System::getenv);
    }

    public static LlmSettings from(Function<String, String> env) {
        String client = first(env, ENV_CLIENT);
        String apiKey = first(env, ENV_API_KEY, ENV_OPENAI_API_KEY);
        String baseUrl = first(env, ENV_BASE_URL);
        if (baseUrl.isBlank() && !apiKey.isBlank()) {
            baseUrl = DEFAULT_OPENAI_URL;
        }
        if (baseUrl.isBlank()) {
            baseUrl = DEFAULT_LOCAL_URL;
        }
        String timeoutRaw = first(env, ENV_TIMEOUT_SECONDS);
        Duration timeout = DEFAULT_TIMEOUT;
        if (!timeoutRaw.isBlank()) {
            timeout = Duration.ofSeconds(Long.parseLong(timeoutRaw.trim()));
        }
        boolean jsonMode = parseBool(first(env, ENV_JSON_MODE), true);
        return new LlmSettings(client, baseUrl, apiKey, first(env, ENV_MODEL), timeout, jsonMode);
    }

    public String resolvedModel() {
        if (!model.isBlank()) {
            return model;
        }
        return looksLikeOfficialOpenAi() ? "gpt-4o-mini" : "llama3.1";
    }

    public boolean hasApiKey() {
        return !apiKey.isBlank() && !"none".equalsIgnoreCase(apiKey) && !"n/a".equalsIgnoreCase(apiKey);
    }

    public boolean looksLikeOfficialOpenAi() {
        String host = baseUrl.toLowerCase(Locale.ROOT);
        return host.contains("api.openai.com");
    }

    /** Safe for logs: host + model, never the key. */
    public String describe() {
        return "baseUrl=" + baseUrl + " model=" + resolvedModel() + " jsonMode=" + jsonMode;
    }

    public static String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return DEFAULT_LOCAL_URL;
        }
        String trimmed = baseUrl.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static String first(Function<String, String> env, String... keys) {
        for (String key : keys) {
            String value = env.apply(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private static boolean parseBool(String raw, boolean fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "1", "true", "yes", "on" -> true;
            case "0", "false", "no", "off" -> false;
            default -> fallback;
        };
    }
}
