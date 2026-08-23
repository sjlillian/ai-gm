package aigm.client;

import java.util.List;
import java.util.Map;

import aigm.gamestate.Clock;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.Crew;
import aigm.llm.gm.LlmActivities;
import aigm.workflow.CampaignWorkflow;
import aigm.workflow.DowntimeActivityChoice;

/**
 * Read model for any UI. Built from workflow queries — never from LLM chat history.
 */
public record CampaignSnapshot(
    String campaignWorkflowId,
    CampaignWorkflow.Phase phase,
    int cycleNumber,
    Crew crew,
    List<Clock> crewClocks,
    List<String> pcWorkflowIds,
    String activeScoreWorkflowId,
    String activeDowntimeWorkflowId,
    Position engagementPosition,
    Map<String, Clock> scoreClocks,
    LlmActivities.Adjudication lastAdjudication,
    Map<String, List<DowntimeActivityChoice>> downtimeChoices
) {}
