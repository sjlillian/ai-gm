package aigm.llm;

import java.util.List;
import java.util.Locale;

/**
 * Quick CLI for checking LLM config and trying Ollama models before running the worker.
 * <pre>
 *   mvn -q exec:java -Dexec.mainClass=aigm.llm.LlmProbe
 *   mvn -q exec:java -Dexec.mainClass=aigm.llm.LlmProbe -Dexec.args="list"
 *   mvn -q exec:java -Dexec.mainClass=aigm.llm.LlmProbe -Dexec.args="test mistral"
 * </pre>
 */
public final class LlmProbe {

    private LlmProbe() {}

    public static void main(String[] args) {
        LlmSettings settings = LlmSettings.fromEnvironment();
        String command = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "config";
        switch (command) {
            case "config" -> printConfig(settings);
            case "list" -> listModels(settings);
            case "test" -> testModel(settings, args.length > 1 ? args[1] : null);
            default -> usage();
        }
    }

    private static void printConfig(LlmSettings settings) {
        LlmClient client = LlmClients.fromEnvironment();
        System.out.println("Resolved client: " + client.describe());
        System.out.println("Env:");
        printEnv(LlmSettings.ENV_CLIENT);
        printEnv(LlmSettings.ENV_BASE_URL);
        printEnv(LlmSettings.ENV_MODEL);
        printEnv(LlmSettings.ENV_JSON_MODE);
        printEnv(LlmSettings.ENV_TIMEOUT_SECONDS);
        if (settings.looksLikeOllama()) {
            System.out.println();
            System.out.println("Tip: run `mvn -q exec:java -Dexec.mainClass=aigm.llm.LlmProbe -Dexec.args=list`"
                + " to see pulled Ollama models.");
        }
    }

    private static void listModels(LlmSettings settings) {
        if (!settings.looksLikeOllama()) {
            System.out.println("Model listing uses Ollama's /api/tags. Set AIGM_LLM_CLIENT=ollama"
                + " or point AIGM_LLM_BASE_URL at Ollama (port 11434).");
            return;
        }
        List<String> models = OllamaModels.list(settings);
        if (models.isEmpty()) {
            System.out.println("No models found. Pull one, e.g. `ollama pull llama3.1`.");
            return;
        }
        System.out.println("Ollama models at " + OllamaModels.ollamaHost(settings.baseUrl()) + ":");
        models.forEach(name -> System.out.println("  " + name));
        String current = settings.resolvedModel();
        System.out.println();
        System.out.println("Current AIGM_LLM_MODEL (or default): " + current);
        System.out.println("Try: mvn -q exec:java -Dexec.mainClass=aigm.llm.LlmProbe -Dexec.args=\"test "
            + models.get(0).split(":")[0] + "\"");
    }

    private static void testModel(LlmSettings settings, String modelOverride) {
        LlmClient client = LlmClients.fromEnvironment();
        String model = modelOverride == null || modelOverride.isBlank()
            ? settings.resolvedModel()
            : modelOverride.trim();
        System.out.println("Testing " + client.describe() + " with model=" + model);
        LlmRequest request = LlmRequest.builder()
            .model(model)
            .addSystem("You are a concise assistant.")
            .addUser("Reply in one sentence about rain in Doskvol.")
            .temperature(0.4)
            .maxTokens(80)
            .build();
        LlmResponse response = client.complete(request);
        System.out.println();
        System.out.println("Response (" + response.model() + ", "
            + response.usage().totalTokens() + " tokens):");
        System.out.println(response.content().trim());
    }

    private static void printEnv(String key) {
        String value = System.getenv(key);
        System.out.println("  " + key + "=" + (value == null || value.isBlank() ? "(unset)" : value));
    }

    private static void usage() {
        System.out.println("""
            Usage:
              config   show resolved LLM client and env (default)
              list     list models from a running Ollama instance
              test [model]  send a short prompt through the configured client

            Environment (see LlmSettings):
              AIGM_LLM_CLIENT=ollama
              AIGM_LLM_BASE_URL=http://127.0.0.1:11434
              AIGM_LLM_MODEL=llama3.1
              AIGM_LLM_JSON_MODE=false
            """);
    }
}
