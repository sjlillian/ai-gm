package aigm.client.temporal;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import aigm.TaskQueues;
import aigm.client.CampaignSnapshot;
import aigm.client.EndScoreCommand;
import aigm.client.GameClient;
import aigm.client.StartScoreCommand;
import aigm.client.WorkflowIds;
import aigm.gamestate.Clock;
import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.Crew;
import aigm.gamestate.json.GameDataConverter;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import aigm.llm.gm.LlmActivities;
import aigm.workflow.ActionRollResult;
import aigm.workflow.CampaignState;
import aigm.workflow.CampaignWorkflow;
import aigm.workflow.DowntimeActivityChoice;
import aigm.workflow.DowntimeWorkflow;
import aigm.workflow.PlayerWorkflow;
import aigm.workflow.ScoreEndRequest;
import aigm.workflow.ScoreRequest;
import aigm.workflow.ScoreWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

/**
 * {@link GameClient} backed by Temporal workflow stubs.
 * UI layers (CLI, Discord, web) should depend on {@link GameClient} only.
 */
public final class TemporalGameClient implements GameClient {

    private final WorkflowServiceStubs service;
    private final WorkflowClient client;
    private final boolean ownsService;
    private String campaignWorkflowId;

    public TemporalGameClient() {
        this.service = WorkflowServiceStubs.newLocalServiceStubs();
        this.client = WorkflowClient.newInstance(
            service,
            WorkflowClientOptions.newBuilder()
                .setDataConverter(GameDataConverter.create())
                .build()
        );
        this.ownsService = true;
    }

    public TemporalGameClient(WorkflowClient client) {
        this.service = null;
        this.client = client;
        this.ownsService = false;
    }

    @Override
    public String startCampaign(Crew crew, String campaignIdOrNull) {
        String workflowId = campaignIdOrNull == null || campaignIdOrNull.isBlank()
            ? "campaign-" + UUID.randomUUID()
            : campaignIdOrNull;

        CampaignWorkflow workflow = client.newWorkflowStub(
            CampaignWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(TaskQueues.GAME)
                .setWorkflowId(workflowId)
                .build()
        );
        WorkflowClient.start(workflow::run, CampaignState.initial(crew));
        this.campaignWorkflowId = workflowId;
        sleepQuietly(750);
        return workflowId;
    }

    @Override
    public void attach(String campaignWorkflowId) {
        if (campaignWorkflowId == null || campaignWorkflowId.isBlank()) {
            throw new IllegalArgumentException("campaignWorkflowId required");
        }
        this.campaignWorkflowId = campaignWorkflowId;
        campaign().getPhase();
    }

    @Override
    public String campaignId() {
        requireAttached();
        return campaignWorkflowId;
    }

    @Override
    public CampaignSnapshot snapshot() {
        requireAttached();
        CampaignWorkflow campaign = campaign();
        CampaignWorkflow.Phase phase = campaign.getPhase();
        int cycle = campaign.getCycleNumber();
        List<String> pcIds = campaign.getPcWorkflowIds();
        Crew crew = campaign.getCrew();
        List<Clock> crewClocks = campaign.getActiveClocks();

        String scoreId = null;
        String downtimeId = null;
        Position engagement = null;
        Map<String, Clock> scoreClocks = Map.of();
        LlmActivities.Adjudication lastAdj = null;
        Map<String, List<DowntimeActivityChoice>> downtimeChoices = Map.of();

        if (phase == CampaignWorkflow.Phase.SCORE) {
            scoreId = WorkflowIds.score(campaignWorkflowId, cycle);
            try {
                ScoreWorkflow score = scoreStub(scoreId);
                engagement = score.getEngagementPosition();
                scoreClocks = score.getClocks();
                lastAdj = score.getLastAdjudication();
            } catch (RuntimeException ignored) {
                // Child may not be queryable immediately after the phase flip.
            }
        } else if (phase == CampaignWorkflow.Phase.DOWNTIME) {
            downtimeId = WorkflowIds.downtime(campaignWorkflowId, cycle);
            try {
                downtimeChoices = downtimeStub(downtimeId).getSubmittedActivities();
            } catch (RuntimeException ignored) {
                // same
            }
        }

        return new CampaignSnapshot(
            campaignWorkflowId,
            phase,
            cycle,
            crew,
            crewClocks,
            pcIds,
            scoreId,
            downtimeId,
            engagement,
            scoreClocks,
            lastAdj,
            downtimeChoices
        );
    }

    @Override
    public void startScore(StartScoreCommand command) {
        requireAttached();
        ScoreRequest request = new ScoreRequest(
            blankTo(command.scoreId(), "score-" + UUID.randomUUID()),
            command.title(),
            command.planType(),
            command.planDetail(),
            command.targetName(),
            command.targetTier(),
            command.engagementDice(),
            campaignWorkflowId,
            List.of()
        );
        campaign().startScore(request);
        sleepQuietly(500);
    }

    @Override
    public LlmActivities.Adjudication adjudicate(String situation, String approach, Action action) {
        return activeScore().adjudicate(situation, approach, action);
    }

    @Override
    public ActionRollResult resolveAction(
        String pcId,
        Action action,
        int actionRating,
        Position position,
        Effect effect,
        boolean push,
        boolean assist,
        String consequence
    ) {
        return activeScore().resolveAction(
            pcId, action, actionRating, position, effect, push, assist, consequence
        );
    }

    @Override
    public void tickClock(String clockId, int segments) {
        activeScore().tickClock(clockId, segments);
    }

    @Override
    public void endScore(EndScoreCommand command) {
        activeScore().endScore(new ScoreEndRequest(
            command.success(),
            command.atWar(),
            command.crewTier(),
            command.heatContext()
        ));
        sleepQuietly(500);
    }

    @Override
    public void chooseDowntimeActivity(String pcId, DowntimeActivityChoice choice) {
        activeDowntime().chooseActivity(pcId, choice);
    }

    @Override
    public void closeDowntime() {
        activeDowntime().closeDowntime();
        sleepQuietly(500);
    }

    @Override
    public Player getPlayer(String pcNameOrWorkflowId) {
        return playerStub(pcNameOrWorkflowId).getState();
    }

    @Override
    public void markTrauma(String pcNameOrWorkflowId, Trauma.Condition condition) {
        playerStub(pcNameOrWorkflowId).markTrauma(condition);
    }

    @Override
    public void endCampaign() {
        requireAttached();
        campaign().endCampaign();
    }

    @Override
    public void close() {
        if (ownsService && service != null) {
            service.shutdown();
        }
    }

    private CampaignWorkflow campaign() {
        requireAttached();
        return client.newWorkflowStub(CampaignWorkflow.class, campaignWorkflowId);
    }

    private ScoreWorkflow scoreStub(String workflowId) {
        return client.newWorkflowStub(ScoreWorkflow.class, workflowId);
    }

    private DowntimeWorkflow downtimeStub(String workflowId) {
        return client.newWorkflowStub(DowntimeWorkflow.class, workflowId);
    }

    private ScoreWorkflow activeScore() {
        CampaignSnapshot snap = snapshot();
        if (snap.phase() != CampaignWorkflow.Phase.SCORE || snap.activeScoreWorkflowId() == null) {
            throw new IllegalStateException("No active score (phase=" + snap.phase() + ")");
        }
        return scoreStub(snap.activeScoreWorkflowId());
    }

    private DowntimeWorkflow activeDowntime() {
        CampaignSnapshot snap = snapshot();
        if (snap.phase() != CampaignWorkflow.Phase.DOWNTIME || snap.activeDowntimeWorkflowId() == null) {
            throw new IllegalStateException("No active downtime (phase=" + snap.phase() + ")");
        }
        return downtimeStub(snap.activeDowntimeWorkflowId());
    }

    private PlayerWorkflow playerStub(String pcNameOrWorkflowId) {
        requireAttached();
        String id = pcNameOrWorkflowId;
        if (!id.startsWith("pc-")) {
            id = WorkflowIds.pc(campaignWorkflowId, pcNameOrWorkflowId);
        }
        return client.newWorkflowStub(PlayerWorkflow.class, id);
    }

    private void requireAttached() {
        if (campaignWorkflowId == null || campaignWorkflowId.isBlank()) {
            throw new IllegalStateException("Not attached to a campaign. Call startCampaign or attach first.");
        }
    }

    private static String blankTo(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
