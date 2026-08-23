package aigm.workflow;

import java.util.List;

import aigm.gamestate.campaign.Heat;

public record DowntimeRequest(
    String downtimeId,
    List<String> pcIds,
    List<String> pcWorkflowIds,
    String campaignWorkflowId,
    int freeActivitiesPerPc,
    Heat.WantedLevel wantedLevel,
    int currentHeat,
    int crewTier
) {

    public DowntimeRequest {
        pcIds = pcIds == null ? List.of() : List.copyOf(pcIds);
        pcWorkflowIds = pcWorkflowIds == null ? List.copyOf(pcIds) : List.copyOf(pcWorkflowIds);
    }

    public DowntimeRequest(String downtimeId, List<String> pcIds) {
        this(
            downtimeId,
            pcIds,
            pcIds,
            "",
            2,
            Heat.WantedLevel.ZERO,
            0,
            0
        );
    }
}
