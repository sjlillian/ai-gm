package aigm.workflow;

import java.util.ArrayList;
import java.util.List;

/** Current wizard step plus legal options for the CLI/UI. */
public record CreationPrompt(
    String step,
    String message,
    List<String> options,
    boolean complete,
    List<PromptOption> choices,
    List<PromptField> fields,
    String selectionMode
) {
    public static final String SINGLE = "SINGLE";
    public static final String PAIR = "PAIR";
    public static final String MAP = "MAP";
    public static final String NONE = "NONE";

    public CreationPrompt {
        step = step == null ? "" : step;
        message = message == null ? "" : message;
        choices = choices == null ? List.of() : List.copyOf(choices);
        fields = fields == null ? List.of() : List.copyOf(fields);
        if (options == null || options.isEmpty()) {
            List<String> ids = new ArrayList<>();
            for (PromptOption choice : choices) {
                ids.add(choice.id());
            }
            options = List.copyOf(ids);
        } else {
            options = List.copyOf(options);
        }
        if (selectionMode == null || selectionMode.isBlank()) {
            selectionMode = choices.isEmpty() ? NONE : SINGLE;
        }
    }

    public CreationPrompt(String step, String message, List<String> options, boolean complete) {
        this(step, message, options, complete, List.of(), List.of(), complete ? NONE : SINGLE);
    }

    public static CreationPrompt of(Enum<?> step, String message, List<String> options) {
        return new CreationPrompt(step.name(), message, options, false);
    }

    public static CreationPrompt choose(Enum<?> step, String message, List<PromptOption> choices) {
        return new CreationPrompt(step.name(), message, List.of(), false, choices, List.of(), SINGLE);
    }

    public static CreationPrompt choose(
        Enum<?> step,
        String message,
        List<PromptOption> choices,
        List<PromptField> fields,
        String selectionMode
    ) {
        return new CreationPrompt(step.name(), message, List.of(), false, choices, fields, selectionMode);
    }

    public static CreationPrompt done(String message) {
        return new CreationPrompt("DONE", message, List.of(), true, List.of(), List.of(), NONE);
    }
}
