package aigm.workflow;

import java.util.List;

import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.score.ScoreType;

public record ScoreRequest(
    String scoreId,
    String title,
    ScoreType planType,
    String planDetail,
    String targetName,
    CrewStanding.Tier targetTier,
    int engagementDice,
    String campaignWorkflowId,
    List<String> pcWorkflowIds
) {
    public ScoreRequest {
        pcWorkflowIds = pcWorkflowIds == null ? List.of() : List.copyOf(pcWorkflowIds);
    }
}
