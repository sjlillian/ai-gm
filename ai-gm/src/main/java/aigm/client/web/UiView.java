package aigm.client.web;

import java.util.List;

import aigm.client.CampaignSnapshot;
import aigm.workflow.CampaignWorkflow;

/**
 * Read model for the HTTP UI. The page renders {@code snapshot} / selected
 * {@code state} and pushes answers through {@code prompt} + {@code actions}.
 */
public record UiView(
    boolean attached,
    String campaignId,
    CampaignWorkflow.Phase phase,
    int cycleNumber,
    CampaignSnapshot snapshot,
    List<UiClient> clients,
    UiClient selected,
    boolean respondable,
    List<UiAction> actions
) {
    public UiView {
        clients = clients == null ? List.of() : List.copyOf(clients);
        actions = actions == null ? List.of() : List.copyOf(actions);
    }

    public static UiView detached() {
        return new UiView(false, null, null, 0, null, List.of(), null, false, List.of());
    }
}
