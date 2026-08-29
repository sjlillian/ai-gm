package aigm.client.web;

import aigm.workflow.CreationPrompt;

/**
 * One seat at the table: the campaign/crew sheet or a PC workflow.
 * {@code state} is the live sheet for that seat (Crew or Player).
 */
public record UiClient(
    String id,
    String kind,
    String label,
    CreationPrompt prompt,
    Object state
) {
    public static final String CAMPAIGN = "campaign";
    public static final String KIND_CAMPAIGN = "CAMPAIGN";
    public static final String KIND_PC = "PC";

    public UiClient {
        id = id == null ? "" : id;
        kind = kind == null ? KIND_CAMPAIGN : kind;
        label = label == null || label.isBlank() ? id : label;
    }
}
