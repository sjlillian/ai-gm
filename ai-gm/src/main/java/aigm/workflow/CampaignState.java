package aigm.workflow;

import java.util.List;

import aigm.gamestate.campaign.Crew;

/** Durable campaign snapshot passed through continueAsNew. */
public record CampaignState(
    Crew crew,
    List<String> pcWorkflowIds,
    int cycleNumber,
    boolean pcsStarted,
    boolean sessionZeroComplete
) {
    public CampaignState {
        crew = crew == null ? Crew.blank() : crew;
        pcWorkflowIds = pcWorkflowIds == null ? List.of() : List.copyOf(pcWorkflowIds);
    }

    /** Demo / already-built crew: skip Session 0, start PC children on first run. */
    public static CampaignState initial(Crew crew) {
        return new CampaignState(crew, List.of(), 0, false, true);
    }

    /** New campaign: sit in SESSION_ZERO until PCs and crew are created. */
    public static CampaignState blank() {
        return new CampaignState(Crew.blank(), List.of(), 0, false, false);
    }

    public CampaignState withCrew(Crew crew) {
        return new CampaignState(crew, pcWorkflowIds, cycleNumber, pcsStarted, sessionZeroComplete);
    }
}
