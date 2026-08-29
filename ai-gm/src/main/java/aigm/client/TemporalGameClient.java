package aigm.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import aigm.TaskQueues;
import aigm.gamestate.Clock;
import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.Crew;
import aigm.gamestate.json.GameDataConverter;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Background;
import aigm.gamestate.player.Heritage;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import aigm.gamestate.player.ViceKind;
import aigm.llm.LlmActivities;
import aigm.workflow.ActionRollResult;
import aigm.workflow.CampaignState;
import aigm.workflow.CampaignWorkflow;
import aigm.workflow.CreationPrompt;
import aigm.workflow.DowntimeActivityChoice;
import aigm.workflow.DowntimeWorkflow;
import aigm.workflow.PlayerWorkflow;
import aigm.workflow.ScoreEndRequest;
import aigm.workflow.ScoreRequest;
import aigm.workflow.ScoreWorkflow;
import aigm.workflow.SessionZeroStatus;
import aigm.workflow.WorkflowSupport;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

/**
 * Talks to Temporal workflow stubs. CLI (and later Discord/web) call this.
 */
public final class TemporalGameClient implements AutoCloseable {

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

    public String startBlankCampaign(String campaignIdOrNull) {
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
        WorkflowClient.start(workflow::run, CampaignState.blank());
        this.campaignWorkflowId = workflowId;
        sleepQuietly(750);
        return workflowId;
    }

    public void attach(String campaignWorkflowId) {
        if (campaignWorkflowId == null || campaignWorkflowId.isBlank()) {
            throw new IllegalArgumentException("campaignWorkflowId required");
        }
        this.campaignWorkflowId = campaignWorkflowId;
        campaign().getPhase();
    }

    public String campaignId() {
        requireAttached();
        return campaignWorkflowId;
    }

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
        SessionZeroStatus sessionZero = null;
        CreationPrompt creationPrompt = null;

        if (phase == CampaignWorkflow.Phase.SCORE) {
            scoreId = WorkflowSupport.scoreWorkflowId(campaignWorkflowId, cycle);
            try {
                ScoreWorkflow score = scoreStub(scoreId);
                engagement = score.getEngagementPosition();
                scoreClocks = score.getClocks();
                lastAdj = score.getLastAdjudication();
            } catch (RuntimeException ignored) {
                // Child may not be queryable immediately after the phase flip.
            }
        } else if (phase == CampaignWorkflow.Phase.DOWNTIME) {
            downtimeId = WorkflowSupport.downtimeWorkflowId(campaignWorkflowId, cycle);
            try {
                downtimeChoices = downtimeStub(downtimeId).getSubmittedActivities();
            } catch (RuntimeException ignored) {
                // same
            }
        } else if (phase == CampaignWorkflow.Phase.SESSION_ZERO) {
            try {
                sessionZero = campaign.getSessionZeroStatus();
                creationPrompt = campaign.getCreationPrompt();
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
            downtimeChoices,
            sessionZero,
            creationPrompt
        );
    }

    public void startScore(ScoreRequest request) {
        requireAttached();
        ScoreRequest filled = new ScoreRequest(
            blankTo(request.scoreId(), "score-" + UUID.randomUUID()),
            request.title(),
            request.planType(),
            request.planDetail(),
            request.targetName(),
            request.targetTier(),
            request.engagementDice(),
            campaignWorkflowId,
            request.pcWorkflowIds()
        );
        campaign().startScore(filled);
        sleepQuietly(500);
    }

    public LlmActivities.Adjudication adjudicate(String situation, String approach, Action action) {
        return activeScore().adjudicate(situation, approach, action);
    }

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

    public void tickClock(String clockId, int segments) {
        activeScore().tickClock(clockId, segments);
    }

    public void endScore(ScoreEndRequest request) {
        activeScore().endScore(request);
        sleepQuietly(500);
    }

    public void chooseDowntimeActivity(String pcId, DowntimeActivityChoice choice) {
        activeDowntime().chooseActivity(pcId, choice);
    }

    public void closeDowntime() {
        activeDowntime().closeDowntime();
        sleepQuietly(500);
    }

    public Player getPlayer(String pcNameOrWorkflowId) {
        return playerStub(pcNameOrWorkflowId).getState();
    }

    public CreationPrompt joinPlayer(String pcId) {
        requireAttached();
        CreationPrompt prompt = campaign().joinPlayer(pcId);
        sleepQuietly(400);
        return prompt;
    }

    public CreationPrompt closeJoining() {
        requireAttached();
        return campaign().closeJoining();
    }

    public CreationPrompt applyCrewCreation(String token, String rest) {
        requireAttached();
        CampaignWorkflow campaign = campaign();
        CreationPrompt current = campaign.getCreationPrompt();
        String step = current.step();
        return switch (step) {
            case "TYPE" -> campaign.chooseCrewType(token);
            case "REPUTATION" -> campaign.chooseReputation(token);
            case "LAIR" -> campaign.setLair(joinToken(token, rest));
            case "HUNTING_GROUNDS" -> campaign.setHuntingGrounds(joinToken(token, rest));
            case "ABILITY" -> campaign.chooseCrewAbility(token);
            case "CONTACT" -> campaign.chooseCrewContact(token);
            case "UPGRADES" -> campaign.chooseUpgrade(token);
            case "NAME" -> campaign.setCrewName(joinToken(token, rest));
            default -> throw new IllegalStateException("Crew creation step is " + step + ": " + current.message());
        };
    }

    public CreationPrompt applyPcCreation(String pcId, String token, String rest) {
        PlayerWorkflow pc = playerStub(pcId);
        return switch (pc.getCreationStep()) {
            case PLAYBOOK -> pc.choosePlaybook(token);
            case HERITAGE -> pc.chooseHeritage(
                Heritage.valueOf(token.toUpperCase().replace('-', '_')),
                rest);
            case BACKGROUND -> pc.chooseBackground(
                Background.valueOf(token.toUpperCase().replace('-', '_')),
                rest);
            case ACTIONS -> pc.assignActionDot(Action.valueOf(token.toUpperCase()));
            case ABILITY -> pc.chooseAbility(token);
            case CONTACTS -> {
                String rival = rest.split("\\s+")[0];
                yield pc.chooseContacts(token, rival);
            }
            case VICE -> pc.chooseVice(
                ViceKind.valueOf(token.toUpperCase()),
                rest);
            case IDENTITY -> {
                String[] parts = rest.isBlank() ? new String[0] : rest.split("\\s+", 2);
                String alias = parts.length > 0 ? parts[0] : "";
                String look = parts.length > 1 ? parts[1] : "";
                yield pc.setIdentity(token, alias, look);
            }
            case DONE -> pc.getCreationPrompt();
        };
    }

    public CreationPrompt getPcCreationPrompt(String pcId) {
        return playerStub(pcId).getCreationPrompt();
    }

    public void markTrauma(String pcNameOrWorkflowId, Trauma.Condition condition) {
        playerStub(pcNameOrWorkflowId).markTrauma(condition);
    }

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
            id = WorkflowSupport.pcWorkflowId(campaignWorkflowId, pcNameOrWorkflowId);
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

    private static String joinToken(String token, String rest) {
        if (rest == null || rest.isBlank()) {
            return token;
        }
        return token + " " + rest;
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
