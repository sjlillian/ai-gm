package aigm.workflow;

import java.util.List;

import aigm.gamestate.campaign.Crew;

/** Durable campaign snapshot passed through continueAsNew. */
public record CampaignState(
    Crew crew,
    List<String> pcWorkflowIds,
    int cycleNumber,
    boolean pcsStarted
) {
    public CampaignState {
        pcWorkflowIds = pcWorkflowIds == null ? List.of() : List.copyOf(pcWorkflowIds);
    }

    public static CampaignState initial(Crew crew) {
        return new CampaignState(crew, List.of(), 0, false);
    }

    public CampaignState withCrew(Crew crew) {
        return new CampaignState(crew, pcWorkflowIds, cycleNumber, pcsStarted);
    }
}
