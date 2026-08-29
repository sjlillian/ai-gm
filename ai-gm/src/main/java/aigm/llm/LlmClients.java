package aigm.llm;

import java.util.Locale;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolves an {@link LlmClient} from env vars. {@code AIGM_LLM_CLIENT} is
 * {@code stub}, {@code ollama}, or {@code openai}; if unset, a configured base
 * URL / model / key selects OpenAI-compatible HTTP, otherwise the stub.
 */
public final class LlmClients {

    private static final Logger log = LoggerFactory.getLogger(LlmClients.class);

    private LlmClients() {}

    public static LlmClient fromEnvironment() {
        return from(System::getenv);
    }

    public static LlmClient from(Function<String, String> env) {
        LlmSettings settings = LlmSettings.from(env);
        String requested = trim(env.apply(LlmSettings.ENV_CLIENT)).toLowerCase(Locale.ROOT);
        LlmClient client;
        if (requested.isEmpty()) {
            client = explicitlyConfigured(env)
                ? new OpenAiCompatibleClient(settings)
                : new StubLlmClient();
        } else if ("stub".equals(requested)) {
            client = new StubLlmClient();
        } else if ("openai".equals(requested) || "ollama".equals(requested)) {
            client = new OpenAiCompatibleClient(settings);
        } else {
            throw LlmException.fatal(
                "Unknown AIGM_LLM_CLIENT '" + requested + "' (use stub, ollama, or openai)",
                0
            );
        }
        if (client instanceof StubLlmClient && requested.isEmpty()) {
            log.warn("No AIGM_LLM_* config; using StubLlmClient. Set AIGM_LLM_CLIENT=ollama / "
                + "AIGM_LLM_MODEL, or AIGM_LLM_API_KEY (cloud), to talk to a model.");
        } else {
            log.info("LLM client: {}", client.describe());
        }
        return client;
    }

    static boolean explicitlyConfigured(Function<String, String> env) {
        return notBlank(env.apply(LlmSettings.ENV_BASE_URL))
            || notBlank(env.apply(LlmSettings.ENV_API_KEY))
            || notBlank(env.apply(LlmSettings.ENV_OPENAI_API_KEY))
            || notBlank(env.apply(LlmSettings.ENV_MODEL));
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
