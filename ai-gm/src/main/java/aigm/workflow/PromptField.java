package aigm.workflow;

import java.util.List;

/** Extra input the current prompt needs (detail, alias, purveyor, etc.). */
public record PromptField(
    String name,
    String label,
    String kind,
    String hint,
    List<PromptOption> options,
    boolean required
) {
    public PromptField {
        name = name == null ? "" : name;
        label = label == null ? name : label;
        kind = kind == null ? "text" : kind;
        hint = hint == null ? "" : hint;
        options = options == null ? List.of() : List.copyOf(options);
    }

    public static PromptField text(String name, String label, String hint, boolean required) {
        return new PromptField(name, label, "text", hint, List.of(), required);
    }

    public static PromptField area(String name, String label, String hint, boolean required) {
        return new PromptField(name, label, "textarea", hint, List.of(), required);
    }
}
