package aigm.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aigm.activities.Activities;
import aigm.gamestate.Clock;
import aigm.gamestate.DiceRoll;
import aigm.gamestate.Position;
import aigm.gamestate.player.Advancement;
import aigm.gamestate.score.ScoreOutcome;
import io.temporal.workflow.Workflow;

/**
 * One score: engagement → free-form action/clock play → payoff + heat → return.
 * Entanglement is rolled in DowntimeImplemented after heat is on the crew sheet.
 */
public class ScoreImplemented implements ScoreWorkflow {

    private ScoreRequest request;
    private final Map<String, Clock> clocks = new HashMap<>();
    private final List<ActionRollResult> actionLog = new ArrayList<>();
    private Position engagementPosition = Position.RISKY;
    private ScoreEndRequest endRequest;
    private boolean ended;

    @Override
    public ScoreResult run(ScoreRequest request) {
        this.request = request;
        Activities activities = WorkflowSupport.activities();
        Activities narrate = WorkflowSupport.llmActivities();

        DiceRoll engagement = activities.rollEngagement(request.engagementDice());
        engagementPosition = WorkflowSupport.engagementPosition(engagement);
        String engagementNote = engagement.isCritical()
            ? "Engagement critical — exceptional advantage. Starting position: Controlled."
            : "Engagement " + engagement.highest() + " → starting position: " + engagementPosition;
        narrate.narrate(request.title(), engagementNote);

        Workflow.await(() -> ended);

        Activities.PayoffResult payoff = activities.determinePayoff(
            request.targetTier().ordinal(),
            endRequest.crewTier(),
            endRequest.atWar()
        );
        Activities.HeatResult heat = activities.determineHeat(endRequest.heatContext());

        if (request.campaignWorkflowId() != null && !request.campaignWorkflowId().isBlank()) {
            CampaignWorkflow campaign = Workflow.newExternalWorkflowStub(
                CampaignWorkflow.class,
                request.campaignWorkflowId()
            );
            if (payoff.coin() != 0) {
                campaign.adjustCoin(payoff.coin());
            }
            if (payoff.rep() != 0) {
                campaign.adjustRep(payoff.rep());
            }
            if (heat.heat() != 0) {
                campaign.adjustHeat(heat.heat());
            }
        }

        String notes = narrate.narrate(
            request.title() + " ends (" + (endRequest.success() ? "success" : "failure") + ")",
            payoff.notes() + " | " + heat.notes()
        );

        return new ScoreResult(new ScoreOutcome(
            request.targetTier().ordinal(),
            payoff.coin(),
            payoff.rep(),
            heat.heat(),
            "",
            notes
        ));
    }

    @Override
    public void tickClock(String clockId, int segments) {
        Clock existing = clocks.get(clockId);
        if (existing == null) {
            int max = Math.max(4, Math.abs(segments));
            clocks.put(clockId, new Clock(clockId, Math.max(0, segments), max));
            return;
        }
        clocks.put(clockId, existing.tick(segments));
    }

    @Override
    public void recordActionRoll(ActionRollResult result) {
        actionLog.add(result);
        if (request == null || result.pcId() == null || result.pcId().isBlank()) {
            return;
        }
        if (result.position() == Position.DESPERATE && result.action() != null) {
            String workflowId = resolvePcWorkflowId(result.pcId());
            if (workflowId != null) {
                PlayerWorkflow pc = Workflow.newExternalWorkflowStub(PlayerWorkflow.class, workflowId);
                Advancement.XpTrack track = switch (result.action().getAttribute()) {
                    case INSIGHT -> Advancement.XpTrack.INSIGHT;
                    case PROWESS -> Advancement.XpTrack.PROWESS;
                    case RESOLVE -> Advancement.XpTrack.RESOLVE;
                };
                pc.markXp(track, 1);
            }
        }
        if (result.pushed()) {
            String workflowId = resolvePcWorkflowId(result.pcId());
            if (workflowId != null) {
                Workflow.newExternalWorkflowStub(PlayerWorkflow.class, workflowId).markStress(2);
            }
        }
    }

    private String resolvePcWorkflowId(String pcId) {
        if (request.pcWorkflowIds() == null) {
            return null;
        }
        for (String id : request.pcWorkflowIds()) {
            if (id != null && (id.equals(pcId) || id.endsWith("-" + pcId))) {
                return id;
            }
        }
        if (request.campaignWorkflowId() != null && !request.campaignWorkflowId().isBlank()) {
            return WorkflowSupport.pcWorkflowId(request.campaignWorkflowId(), pcId);
        }
        return null;
    }

    @Override
    public void endScore(ScoreEndRequest end) {
        this.endRequest = end;
        this.ended = true;
    }

    @Override
    public Map<String, Clock> getClocks() {
        return Map.copyOf(clocks);
    }

    @Override
    public Position getEngagementPosition() {
        return engagementPosition;
    }
}
