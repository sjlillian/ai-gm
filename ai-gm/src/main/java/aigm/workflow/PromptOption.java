package aigm.workflow;

/** One legal pick on a creation/play prompt. */
public record PromptOption(
    String id,
    String label,
    String description,
    String group
) {
    public PromptOption {
        id = id == null ? "" : id;
        label = label == null || label.isBlank() ? id : label;
        description = description == null ? "" : description;
        group = group == null ? "" : group;
    }

    public PromptOption(String id, String label, String description) {
        this(id, label, description, "");
    }
}
