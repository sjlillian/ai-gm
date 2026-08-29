package aigm.client.web;

import java.util.List;

/** Named command the current client may send. Rendered from this schema, not baked into HTML. */
public record UiAction(
    String id,
    String label,
    List<UiField> fields
) {
    public UiAction {
        id = id == null ? "" : id;
        label = label == null ? id : label;
        fields = fields == null ? List.of() : List.copyOf(fields);
    }
}
