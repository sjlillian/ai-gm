package aigm.workflow;

import java.util.List;

/** Current wizard step plus legal options for the CLI/UI. */
public record CreationPrompt(
    String step,
    String message,
    List<String> options,
    boolean complete
) {
    public CreationPrompt {
        step = step == null ? "" : step;
        message = message == null ? "" : message;
        options = options == null ? List.of() : List.copyOf(options);
    }

    public static CreationPrompt of(Enum<?> step, String message, List<String> options) {
        return new CreationPrompt(step.name(), message, options, false);
    }

    public static CreationPrompt done(String message) {
        return new CreationPrompt("DONE", message, List.of(), true);
    }
}
