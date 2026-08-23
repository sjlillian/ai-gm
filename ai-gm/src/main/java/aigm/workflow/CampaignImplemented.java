package aigm.workflow;

import java.util.ArrayList;
import java.util.List;

import aigm.gamestate.Clock;
import aigm.gamestate.campaign.Claim;
import aigm.gamestate.campaign.Crew;
import aigm.gamestate.player.Player;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

/**
 * Campaign loop: free play → score child → downtime child → continueAsNew.
 * Owns crew sheet mutations; PC children are abandoned across continueAsNew.
 */
public class CampaignImplemented implements CampaignWorkflow {

    private Crew crew;
    private Phase phase = Phase.FREEPLAY;
    private ScoreRequest pendingScore;
    private boolean ended;
    private List<String> pcWorkflowIds = new ArrayList<>();
    private int cycleNumber;
    private boolean pcsStarted;

    @Override
    public void run(CampaignState state) {
        this.crew = state.crew();
        this.pcWorkflowIds = new ArrayList<>(state.pcWorkflowIds());
        this.cycleNumber = state.cycleNumber();
        this.pcsStarted = state.pcsStarted();

        ensurePlayerWorkflows();

        while (!ended) {
            phase = Phase.FREEPLAY;
            Workflow.await(() -> ended || pendingScore != null);
            if (ended) {
                return;
            }

            ScoreRequest scoreRequest = withCampaignIds(pendingScore);
            pendingScore = null;

            phase = Phase.SCORE;
            ScoreWorkflow score = Workflow.newChildWorkflowStub(
                ScoreWorkflow.class,
                ChildWorkflowOptions.newBuilder()
                    .setWorkflowId("score-" + Workflow.getInfo().getWorkflowId() + "-" + cycleNumber)
                    .build()
            );
            score.run(scoreRequest);

            phase = Phase.DOWNTIME;
            DowntimeWorkflow downtime = Workflow.newChildWorkflowStub(
                DowntimeWorkflow.class,
                ChildWorkflowOptions.newBuilder()
                    .setWorkflowId("downtime-" + Workflow.getInfo().getWorkflowId() + "-" + cycleNumber)
                    .build()
            );
            downtime.run(new DowntimeRequest(
                "downtime-" + cycleNumber,
                pcNames(),
                List.copyOf(pcWorkflowIds),
                Workflow.getInfo().getWorkflowId(),
                2,
                this.crew.heat().wantedLevel(),
                this.crew.heat().heat().progress(),
                this.crew.crewStanding().tier().ordinal()
            ));

            cycleNumber++;
            phase = Phase.FREEPLAY;

            if (ended) {
                return;
            }
            Workflow.continueAsNew(snapshot());
        }
    }

    private CampaignState snapshot() {
        return new CampaignState(crew, List.copyOf(pcWorkflowIds), cycleNumber, pcsStarted);
    }

    private void ensurePlayerWorkflows() {
        String campaignId = Workflow.getInfo().getWorkflowId();
        if (!pcsStarted) {
            pcWorkflowIds = new ArrayList<>();
            for (Player member : crew.members()) {
                String workflowId = WorkflowSupport.pcWorkflowId(campaignId, member.name());
                pcWorkflowIds.add(workflowId);
                PlayerWorkflow pc = Workflow.newChildWorkflowStub(
                    PlayerWorkflow.class,
                    ChildWorkflowOptions.newBuilder()
                        .setWorkflowId(workflowId)
                        .setParentClosePolicy(ParentClosePolicy.PARENT_CLOSE_POLICY_ABANDON)
                        .build()
                );
                Async.procedure(pc::run, member);
                Promise<WorkflowExecution> started = Workflow.getWorkflowExecution(pc);
                started.get();
            }
            pcsStarted = true;
            return;
        }
        if (pcWorkflowIds.isEmpty()) {
            for (Player member : crew.members()) {
                pcWorkflowIds.add(WorkflowSupport.pcWorkflowId(campaignId, member.name()));
            }
        }
    }

    private ScoreRequest withCampaignIds(ScoreRequest request) {
        return new ScoreRequest(
            request.scoreId(),
            request.title(),
            request.planType(),
            request.planDetail(),
            request.targetName(),
            request.targetTier(),
            request.engagementDice(),
            Workflow.getInfo().getWorkflowId(),
            List.copyOf(pcWorkflowIds)
        );
    }

    private List<String> pcNames() {
        List<String> names = new ArrayList<>();
        for (Player member : crew.members()) {
            names.add(member.name());
        }
        return names;
    }

    @Override
    public void startScore(ScoreRequest request) {
        this.pendingScore = request;
    }

    @Override
    public void adjustHeat(int delta) {
        crew = crew.updateHeat(delta);
    }

    @Override
    public void adjustCoin(int delta) {
        if (delta >= 0) {
            crew = crew.addCoin(delta);
        } else {
            crew = crew.spendCoin(-delta).orElse(crew);
        }
    }

    @Override
    public void adjustRep(int delta) {
        crew = crew.addRep(delta);
        if (crew.crewStanding().rep().isComplete()) {
            crew = crew.tryAdvance();
        }
    }

    @Override
    public void addClaim(Claim claim) {
        crew = crew.addClaim(claim);
    }

    @Override
    public void addCrewAsset(String asset) {
        crew = crew.addClock(new Clock("Asset: " + asset, 1, 1));
    }

    @Override
    public void endCampaign() {
        ended = true;
    }

    @Override
    public Crew getCrew() {
        return crew;
    }

    @Override
    public List<Clock> getActiveClocks() {
        return crew.clocks();
    }

    @Override
    public Phase getPhase() {
        return phase;
    }

    @Override
    public int getCycleNumber() {
        return cycleNumber;
    }

    @Override
    public java.util.List<String> getPcWorkflowIds() {
        return List.copyOf(pcWorkflowIds);
    }
}