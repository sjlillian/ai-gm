package aigm.client.web;

import java.util.List;

/** One input on a schema-driven action form. The browser does not hard-code game fields. */
public record UiField(
    String name,
    String label,
    String kind,
    List<String> options,
    boolean required
) {
    public UiField {
        name = name == null ? "" : name;
        label = label == null ? name : label;
        kind = kind == null ? "text" : kind;
        options = options == null ? List.of() : List.copyOf(options);
    }

    public static UiField text(String name, String label, boolean required) {
        return new UiField(name, label, "text", List.of(), required);
    }

    public static UiField number(String name, String label, boolean required) {
        return new UiField(name, label, "number", List.of(), required);
    }

    public static UiField select(String name, String label, List<String> options, boolean required) {
        return new UiField(name, label, "select", options, required);
    }

    public static UiField checkbox(String name, String label) {
        return new UiField(name, label, "checkbox", List.of(), false);
    }
}
