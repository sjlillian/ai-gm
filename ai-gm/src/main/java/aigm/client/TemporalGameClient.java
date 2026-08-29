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
import aigm.workflow.ScoreOpportunity;
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

    public boolean isAttached() {
        return campaignWorkflowId != null && !campaignWorkflowId.isBlank();
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
        String worldBrief = "";
        List<ScoreOpportunity> opportunities = List.of();
        String lastInvestigation = "";
        String downtimeEntanglement = "";

        try {
            worldBrief = campaign.getWorldBrief();
            opportunities = campaign.getOpportunities();
            lastInvestigation = campaign.getLastInvestigation();
        } catch (RuntimeException ignored) {
            // Older in-flight campaigns may not have these queries yet.
        }

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
                DowntimeWorkflow downtime = downtimeStub(downtimeId);
                downtimeChoices = downtime.getSubmittedActivities();
                downtimeEntanglement = downtime.getEntanglement();
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
            creationPrompt,
            worldBrief,
            opportunities,
            lastInvestigation,
            downtimeEntanglement
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
        sleepQuietly(1200);
    }

    public void chooseDowntimeActivity(String pcId, DowntimeActivityChoice choice) {
        activeDowntime().submitActivity(pcId, choice);
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
        return applyCrewCreation(token, rest, Map.of());
    }

    public CreationPrompt applyCrewCreation(String token, String rest, Map<String, String> fields) {
        requireAttached();
        Map<String, String> extra = fields == null ? Map.of() : fields;
        CampaignWorkflow campaign = campaign();
        CreationPrompt current = campaign.getCreationPrompt();
        String step = current.step();
        String choice = first(extra, "token", token);
        String detail = first(extra, "detail", rest);
        return switch (step) {
            case "WAITING_FOR_JOIN" -> campaign.joinPlayer(first(extra, "name", joinToken(choice, detail)));
            case "TYPE" -> campaign.chooseCrewType(choice);
            case "REPUTATION" -> campaign.chooseReputation(choice);
            case "LAIR" -> campaign.setLair(place(choice, detail));
            case "HUNTING_GROUNDS" -> campaign.setHuntingGrounds(place(choice, detail));
            case "ABILITY" -> campaign.chooseCrewAbility(choice);
            case "CONTACT" -> campaign.chooseCrewContact(choice);
            case "UPGRADES" -> campaign.chooseUpgrade(choice);
            case "NAME" -> campaign.setCrewName(first(extra, "name", joinToken(choice, detail)));
            default -> throw new IllegalStateException("Crew creation step is " + step + ": " + current.message());
        };
    }

    public CreationPrompt applyPcCreation(String pcId, String token, String rest) {
        return applyPcCreation(pcId, token, rest, Map.of());
    }

    public CreationPrompt applyPcCreation(String pcId, String token, String rest, Map<String, String> fields) {
        Map<String, String> extra = fields == null ? Map.of() : fields;
        PlayerWorkflow pc = playerStub(pcId);
        return switch (pc.getCreationStep()) {
            case PLAYBOOK -> pc.choosePlaybook(first(extra, "token", token));
            case HERITAGE -> pc.chooseHeritage(
                Heritage.valueOf(first(extra, "token", token).toUpperCase().replace('-', '_')),
                first(extra, "detail", rest));
            case BACKGROUND -> pc.chooseBackground(
                Background.valueOf(first(extra, "token", token).toUpperCase().replace('-', '_')),
                first(extra, "detail", rest));
            case ACTIONS -> pc.assignActionDot(Action.valueOf(first(extra, "token", token).toUpperCase()));
            case ABILITY -> pc.chooseAbility(first(extra, "token", token));
            case CONTACTS -> pc.chooseContacts(
                first(extra, "friend", token),
                first(extra, "rival", firstWord(rest)));
            case VICE -> pc.chooseVice(
                ViceKind.valueOf(first(extra, "token", token).toUpperCase()),
                firstNonBlank(extra.get("purveyorCustom"), extra.get("purveyor"), rest));
            case IDENTITY -> {
                String name = first(extra, "name", token);
                String alias = extra.get("alias");
                String look = extra.get("look");
                if (alias == null || alias.isBlank() || look == null || look.isBlank()) {
                    String[] parts = rest.isBlank() ? new String[0] : rest.split("\\s+", 2);
                    if (alias == null || alias.isBlank()) {
                        alias = parts.length > 0 ? parts[0] : "";
                    }
                    if (look == null || look.isBlank()) {
                        look = parts.length > 1 ? parts[1] : rest;
                    }
                }
                yield pc.setIdentity(name, alias, look);
            }
            case DONE -> pc.getCreationPrompt();
        };
    }

    public CreationPrompt getPcCreationPrompt(String pcId) {
        return playerStub(pcId).getCreationPrompt();
    }

    /**
     * Push a wizard answer for the selected client. {@code campaign} (or blank)
     * applies crew creation; anything else is a PC join id.
     */
    public CreationPrompt applyResponse(String clientId, String token, String rest) {
        return applyResponse(clientId, token, rest, Map.of());
    }

    public CreationPrompt applyResponse(
        String clientId,
        String token,
        String rest,
        Map<String, String> fields
    ) {
        if (clientId == null || clientId.isBlank() || "campaign".equalsIgnoreCase(clientId)) {
            return applyCrewCreation(token, rest, fields);
        }
        return applyPcCreation(clientId, token, rest, fields);
    }

    public String investigate(String question) {
        requireAttached();
        return campaign().investigate(question);
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
        if (token == null || token.isBlank()) {
            return rest == null ? "" : rest.trim();
        }
        if (rest == null || rest.isBlank()) {
            return token;
        }
        return token + " " + rest;
    }

    private static String place(String district, String detail) {
        String where = district == null ? "" : district.trim();
        String extra = detail == null ? "" : detail.trim();
        if (where.isBlank()) {
            return extra;
        }
        if (extra.isBlank() || extra.equalsIgnoreCase(where)) {
            return where;
        }
        if (extra.toLowerCase().startsWith(where.toLowerCase())) {
            return extra;
        }
        return where + " — " + extra;
    }

    private static String first(Map<String, String> fields, String key, String fallback) {
        if (fields != null) {
            String value = fields.get(key);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return fallback == null ? "" : fallback.trim();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    private static String firstWord(String rest) {
        if (rest == null || rest.isBlank()) {
            return "";
        }
        String trimmed = rest.trim();
        int space = trimmed.indexOf(' ');
        return space < 0 ? trimmed : trimmed.substring(0, space);
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
