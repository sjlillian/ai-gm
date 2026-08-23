package aigm.llm;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aigm.llm.openai.OpenAiCompatibleClient;
import aigm.llm.stub.StubLlmClient;

/**
 * Resolves an {@link LlmClient} from env vars, {@link ServiceLoader} providers,
 * or a fully-qualified class name.
 */
public final class LlmClients {

    private static final Logger log = LoggerFactory.getLogger(LlmClients.class);

    private LlmClients() {}

    public static LlmClient fromEnvironment() {
        return from(System::getenv);
    }

    public static LlmClient from(Function<String, String> env) {
        LlmSettings settings = LlmSettings.from(env);
        String requested = trim(env.apply(LlmSettings.ENV_CLIENT));
        if (!requested.isEmpty()) {
            LlmClient client = create(requested, settings);
            log.info("LLM client '{}': {}", requested, client.describe());
            return client;
        }
        if (explicitlyConfigured(env)) {
            LlmClient client = new OpenAiCompatibleClient(settings);
            log.info("LLM client 'openai': {}", client.describe());
            return client;
        }
        log.warn("No AIGM_LLM_* config; using StubLlmClient. Set AIGM_LLM_BASE_URL / "
            + "AIGM_LLM_MODEL (local) or AIGM_LLM_API_KEY (cloud) to talk to a model.");
        return new StubLlmClient();
    }

    public static LlmClient create(String clientIdOrClass, LlmSettings settings) {
        String id = clientIdOrClass.trim();
        LlmClientProvider provider = providers().get(id.toLowerCase(Locale.ROOT));
        if (provider != null) {
            return provider.create(settings);
        }
        return instantiate(id, settings);
    }

    public static LlmClient openaiCompatible(LlmSettings settings) {
        return new OpenAiCompatibleClient(settings);
    }

    public static LlmClient stub() {
        return new StubLlmClient();
    }

    static boolean explicitlyConfigured(Function<String, String> env) {
        return notBlank(env.apply(LlmSettings.ENV_BASE_URL))
            || notBlank(env.apply(LlmSettings.ENV_API_KEY))
            || notBlank(env.apply(LlmSettings.ENV_OPENAI_API_KEY))
            || notBlank(env.apply(LlmSettings.ENV_MODEL));
    }

    private static Map<String, LlmClientProvider> providers() {
        Map<String, LlmClientProvider> map = new LinkedHashMap<>();
        register(map, new OpenAiCompatibleClient.Provider());
        map.put("openai-compatible", new OpenAiCompatibleClient.Provider());
        register(map, new StubLlmClient.Provider());
        ServiceLoader.load(LlmClientProvider.class).forEach(provider -> register(map, provider));
        return map;
    }

    private static void register(Map<String, LlmClientProvider> map, LlmClientProvider provider) {
        map.put(provider.id().toLowerCase(Locale.ROOT), provider);
    }

    private static LlmClient instantiate(String className, LlmSettings settings) {
        try {
            Class<?> type = Class.forName(className);
            Object instance = construct(type, settings);
            if (instance instanceof LlmClientProvider provider) {
                return provider.create(settings);
            }
            if (instance instanceof LlmClient client) {
                return client;
            }
            throw LlmException.fatal(
                className + " is not an LlmClient or LlmClientProvider",
                0
            );
        } catch (LlmException e) {
            throw e;
        } catch (Exception e) {
            throw LlmException.fatal("Could not load LLM client " + className + ": " + e.getMessage(), 0);
        }
    }

    private static Object construct(Class<?> type, LlmSettings settings) throws Exception {
        Constructor<?> withSettings = findConstructor(type, LlmSettings.class);
        if (withSettings != null) {
            return withSettings.newInstance(settings);
        }
        Constructor<?> noArg = findConstructor(type);
        if (noArg != null) {
            return noArg.newInstance();
        }
        throw new NoSuchMethodException("Need LlmSettings or no-arg constructor on " + type.getName());
    }

    private static Constructor<?> findConstructor(Class<?> type, Class<?>... params) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructor(params);
            ctor.setAccessible(true);
            return ctor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
