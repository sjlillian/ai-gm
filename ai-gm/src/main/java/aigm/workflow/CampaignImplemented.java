package aigm.workflow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import aigm.gamestate.Ability;
import aigm.gamestate.Clock;
import aigm.gamestate.Contact;
import aigm.gamestate.campaign.Claim;
import aigm.gamestate.campaign.Crew;
import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.campaign.CrewTypeEnum;
import aigm.gamestate.campaign.Upgrade;
import aigm.gamestate.player.Player;
import aigm.gamestate.score.ScoreType;
import aigm.llm.LlmActivities;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.ParentClosePolicy;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

/**
 * Campaign loop: optional Session 0, then free play → score child → downtime child → continueAsNew.
 * Owns crew sheet mutations; PC children are abandoned across continueAsNew.
 */
public class CampaignImplemented implements CampaignWorkflow {

    private static final int EXTRA_UPGRADES = 2;

    private Crew crew;
    private Phase phase = Phase.FREEPLAY;
    private ScoreRequest pendingScore;
    private boolean ended;
    private List<String> pcWorkflowIds = new ArrayList<>();
    private int cycleNumber;
    private boolean pcsStarted;
    private boolean sessionZeroComplete;

    private CrewCreationStep crewStep = CrewCreationStep.DONE;
    private boolean joiningClosed;
    private final List<String> joinedPcIds = new ArrayList<>();
    private final Set<String> readyPcIds = new HashSet<>();
    private int extraUpgradesPicked;
    private String worldBrief = "";
    private List<ScoreOpportunity> opportunities = new ArrayList<>();
    private String lastInvestigation = "";

    @Override
    public void run(CampaignState state) {
        this.crew = state.crew();
        this.pcWorkflowIds = new ArrayList<>(state.pcWorkflowIds());
        this.cycleNumber = state.cycleNumber();
        this.pcsStarted = state.pcsStarted();
        this.sessionZeroComplete = state.sessionZeroComplete();
        this.worldBrief = state.worldBrief();
        this.opportunities = new ArrayList<>(state.opportunities());
        this.lastInvestigation = state.lastInvestigation();

        if (!sessionZeroComplete) {
            phase = Phase.SESSION_ZERO;
            runSessionZero();
            if (ended) {
                return;
            }
            sessionZeroComplete = true;
            pcsStarted = true;
            Workflow.continueAsNew(snapshot());
            return;
        }

        ensurePlayerWorkflows();
        if (worldBrief == null || worldBrief.isBlank()) {
            startingSituation();
        }

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
                    .setWorkflowId(WorkflowSupport.scoreWorkflowId(Workflow.getInfo().getWorkflowId(), cycleNumber))
                    .build()
            );
            score.run(scoreRequest);

            phase = Phase.DOWNTIME;
            DowntimeWorkflow downtime = Workflow.newChildWorkflowStub(
                DowntimeWorkflow.class,
                ChildWorkflowOptions.newBuilder()
                    .setWorkflowId(WorkflowSupport.downtimeWorkflowId(Workflow.getInfo().getWorkflowId(), cycleNumber))
                    .build()
            );
            downtime.run(new DowntimeRequest(
                "downtime-" + cycleNumber,
                pcJoinIds(),
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

    private void runSessionZero() {
        crewStep = CrewCreationStep.WAITING_FOR_JOIN;
        Workflow.await(() -> ended || (joiningClosed && !joinedPcIds.isEmpty()));
        if (ended) {
            return;
        }
        crewStep = CrewCreationStep.WAITING_FOR_PCS;
        Workflow.await(() -> ended || allJoinedPcsReady());
        if (ended) {
            return;
        }
        crewCreation();
        if (ended) {
            return;
        }
        startingSituation();
        crewStep = CrewCreationStep.DONE;
    }

    private void crewCreation() {
        crewStep = CrewCreationStep.TYPE;
        Workflow.await(() -> ended || crewStep == CrewCreationStep.DONE);
    }

    private void startingSituation() {
        LlmActivities.StartingSituation situation = WorkflowSupport.llmActivities()
            .generateStartingSituation(crewSummary());
        if (situation.clocks() != null) {
            for (LlmActivities.ClockSpec spec : situation.clocks()) {
                if (spec == null || spec.name() == null || spec.name().isBlank()) {
                    continue;
                }
                int max = spec.segments() < 1 ? 4 : spec.segments();
                crew = crew.addClock(new Clock(spec.name(), 0, max));
            }
        }
        if (situation.factions() != null) {
            for (LlmActivities.FactionNote note : situation.factions()) {
                if (note == null || note.faction() == null || note.faction().isBlank() || note.status() == null) {
                    continue;
                }
                crew = crew.setFactionStatus(note.faction(), note.status());
            }
        }
        worldBrief = situation.fiction() == null ? "" : situation.fiction();
        opportunities = new ArrayList<>();
        if (situation.scores() != null) {
            int n = 1;
            for (LlmActivities.ScoreSeed seed : situation.scores()) {
                if (seed == null || seed.title() == null || seed.title().isBlank()) {
                    continue;
                }
                opportunities.add(new ScoreOpportunity(
                    "opp-" + n++,
                    seed.title(),
                    seed.hook(),
                    seed.targetName(),
                    parseOpportunityTier(seed.targetTier()),
                    parseOpportunityPlan(seed.planType()),
                    seed.district()
                ));
            }
        }
        if (opportunities.isEmpty()) {
            opportunities.add(new ScoreOpportunity(
                "opp-1",
                "A job in " + (crew.huntingGrounds().isBlank() ? "Crow's Foot" : crew.huntingGrounds()),
                "Someone wants work done on your turf. Ask around.",
                "Unknown patron",
                CrewStanding.Tier.ZERO,
                ScoreType.STEALTH,
                crew.huntingGrounds()
            ));
        }
        WorkflowSupport.llmActivities().narrate("Session 0 starting situation", situation.fiction());
    }

    private String crewSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Crew: ").append(crew.name());
        if (crew.type() != null) {
            sb.append(" (").append(crew.type().getType()).append(')');
        }
        sb.append("\nLair: ").append(crew.lair());
        sb.append("\nHunting grounds: ").append(crew.huntingGrounds());
        sb.append("\nReputation: ").append(crew.crewStanding().reputation());
        sb.append("\nMembers:\n");
        for (Player member : crew.members()) {
            sb.append("- ").append(member.name());
            if (member.playbook() != null) {
                sb.append(" [").append(member.playbook().getName()).append(']');
            }
            if (member.heritage() != null) {
                sb.append(" ").append(member.heritage().getName());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private boolean allJoinedPcsReady() {
        return !joinedPcIds.isEmpty() && readyPcIds.containsAll(joinedPcIds);
    }

    private CampaignState snapshot() {
        return new CampaignState(
            crew,
            List.copyOf(pcWorkflowIds),
            cycleNumber,
            pcsStarted,
            sessionZeroComplete,
            worldBrief,
            List.copyOf(opportunities),
            lastInvestigation
        );
    }

    private List<String> pcJoinIds() {
        List<String> ids = new ArrayList<>();
        String campaignId = Workflow.getInfo().getWorkflowId();
        String prefix = "pc-" + campaignId + "-";
        for (String workflowId : pcWorkflowIds) {
            if (workflowId != null && workflowId.startsWith(prefix)) {
                ids.add(workflowId.substring(prefix.length()));
            } else if (workflowId != null && !workflowId.isBlank()) {
                ids.add(workflowId);
            }
        }
        if (ids.isEmpty()) {
            ids.addAll(pcNames());
        }
        return ids;
    }

    private static CrewStanding.Tier parseOpportunityTier(String raw) {
        if (raw == null || raw.isBlank()) {
            return CrewStanding.Tier.ZERO;
        }
        try {
            return CrewStanding.Tier.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CrewStanding.Tier.ZERO;
        }
    }

    private static ScoreType parseOpportunityPlan(String raw) {
        if (raw == null || raw.isBlank()) {
            return ScoreType.STEALTH;
        }
        try {
            return ScoreType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ScoreType.STEALTH;
        }
    }

    private void ensurePlayerWorkflows() {
        String campaignId = Workflow.getInfo().getWorkflowId();
        if (!pcsStarted) {
            pcWorkflowIds = new ArrayList<>();
            for (Player member : crew.members()) {
                String workflowId = WorkflowSupport.pcWorkflowId(campaignId, member.name());
                pcWorkflowIds.add(workflowId);
                startPlayerChild(workflowId, member);
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

    private void startPlayerChild(String workflowId, Player member) {
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

    private void requireSessionZero() {
        if (sessionZeroComplete || phase != Phase.SESSION_ZERO) {
            throw new IllegalStateException("Campaign is not in Session 0");
        }
    }

    private void requireCrewStep(CrewCreationStep expected) {
        requireSessionZero();
        if (crewStep != expected) {
            throw new IllegalStateException("Expected " + expected + ", currently " + crewStep);
        }
    }

    @Override
    public CreationPrompt joinPlayer(String pcId) {
        requireSessionZero();
        if (joiningClosed) {
            throw new IllegalStateException("Joining is closed");
        }
        if (crewStep != CrewCreationStep.WAITING_FOR_JOIN) {
            throw new IllegalStateException("Not accepting joins (step " + crewStep + ")");
        }
        if (pcId == null || pcId.isBlank()) {
            throw new IllegalArgumentException("pcId required");
        }
        String id = pcId.trim();
        if (joinedPcIds.contains(id)) {
            throw new IllegalStateException("Already joined: " + id);
        }
        String workflowId = WorkflowSupport.pcWorkflowId(Workflow.getInfo().getWorkflowId(), id);
        startPlayerChild(workflowId, Player.draft(id));
        joinedPcIds.add(id);
        pcWorkflowIds.add(workflowId);
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt closeJoining() {
        requireSessionZero();
        if (joinedPcIds.isEmpty()) {
            throw new IllegalStateException("Join at least one PC before closing");
        }
        joiningClosed = true;
        return getCreationPrompt();
    }

    @Override
    public void pcCreationComplete(String pcId, Player player) {
        if (pcId == null || pcId.isBlank() || player == null) {
            return;
        }
        readyPcIds.add(pcId);
        boolean replaced = false;
        List<Player> members = new ArrayList<>(crew.members());
        for (int i = 0; i < members.size(); i++) {
            if (pcId.equals(members.get(i).name()) || player.name().equals(members.get(i).name())) {
                members.set(i, player);
                replaced = true;
                break;
            }
        }
        if (replaced) {
            crew = crew.withMembers(members);
        } else {
            crew = crew.addMember(player);
        }
    }

    @Override
    public CreationPrompt chooseCrewType(String typeName) {
        requireCrewStep(CrewCreationStep.TYPE);
        CrewTypeEnum type = CreationCatalog.crewType(typeName);
        crew = crew.withType(type);
        for (Upgrade upgrade : type.getStartingUpgrades()) {
            crew = crew.addUpgrade(upgrade);
        }
        extraUpgradesPicked = 0;
        crewStep = CrewCreationStep.REPUTATION;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt chooseReputation(String reputation) {
        requireCrewStep(CrewCreationStep.REPUTATION);
        CrewStanding.Reputation value = CreationCatalog.reputation(reputation);
        crew = crew.withReputation(value);
        crewStep = CrewCreationStep.LAIR;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt setLair(String lair) {
        requireCrewStep(CrewCreationStep.LAIR);
        if (lair == null || lair.isBlank()) {
            throw new IllegalArgumentException("lair required");
        }
        crew = crew.withLair(lair.trim());
        crewStep = CrewCreationStep.HUNTING_GROUNDS;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt setHuntingGrounds(String huntingGrounds) {
        requireCrewStep(CrewCreationStep.HUNTING_GROUNDS);
        if (huntingGrounds == null || huntingGrounds.isBlank()) {
            throw new IllegalArgumentException("hunting grounds required");
        }
        crew = crew.withHuntingGrounds(huntingGrounds.trim());
        crewStep = CrewCreationStep.ABILITY;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt chooseCrewAbility(String abilityName) {
        requireCrewStep(CrewCreationStep.ABILITY);
        if (crew.type() == null) {
            throw new IllegalStateException("Choose a crew type first");
        }
        Ability ability = CreationCatalog.ability(crew.type().getAbilities(), abilityName);
        crew = crew.addAbility(ability);
        crewStep = CrewCreationStep.CONTACT;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt chooseCrewContact(String contactName) {
        requireCrewStep(CrewCreationStep.CONTACT);
        if (crew.type() == null) {
            throw new IllegalStateException("Choose a crew type first");
        }
        Contact contact = CreationCatalog.contact(crew.type().getContacts(), contactName);
        crew = crew.addContact(contact);
        crewStep = CrewCreationStep.UPGRADES;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt chooseUpgrade(String upgradeName) {
        requireCrewStep(CrewCreationStep.UPGRADES);
        Upgrade upgrade = CreationCatalog.upgrade(CreationCatalog.upgradePool(crew.type()), upgradeName);
        for (Upgrade owned : crew.upgrades()) {
            if (owned.getName().equalsIgnoreCase(upgrade.getName())) {
                throw new IllegalArgumentException("Crew already has " + upgrade.getName());
            }
        }
        crew = crew.addUpgrade(upgrade);
        extraUpgradesPicked++;
        if (extraUpgradesPicked >= EXTRA_UPGRADES) {
            crewStep = CrewCreationStep.NAME;
        }
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt setCrewName(String name) {
        requireCrewStep(CrewCreationStep.NAME);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("crew name required");
        }
        crew = crew.withName(name.trim());
        crewStep = CrewCreationStep.DONE;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt getCreationPrompt() {
        if (sessionZeroComplete || phase != Phase.SESSION_ZERO) {
            return CreationPrompt.done("Session 0 is complete.");
        }
        return switch (crewStep) {
            case WAITING_FOR_JOIN -> CreationPrompt.choose(
                crewStep,
                "Each player joins with their name (the username they'll play as). "
                    + "When everyone is in, close joining. Joined: " + joinedPcIds,
                List.of(),
                List.of(PromptField.text(
                    "name",
                    "Your name",
                    "Not a character sheet name yet — just how you'll sit at this table.",
                    true)),
                CreationPrompt.NONE);
            case WAITING_FOR_PCS -> CreationPrompt.of(
                crewStep,
                "Waiting for character creation. Ready: " + readyPcIds + " of " + joinedPcIds,
                List.of());
            case TYPE -> CreationPrompt.choose(
                crewStep,
                "What kind of criminal enterprise is this crew?",
                CreationCatalog.crewTypeOptions());
            case REPUTATION -> CreationPrompt.choose(
                crewStep,
                "How does the underworld already talk about you?",
                CreationCatalog.reputationOptions());
            case LAIR -> CreationPrompt.choose(
                crewStep,
                "Where is the lair? Pick a district on the map, then say where in that district you actually sleep.",
                CreationCatalog.districtOptions(),
                List.of(PromptField.text(
                    "detail",
                    "The room itself",
                    "A loft above a butcher, a barge hold, a ruined bell tower…",
                    true)),
                CreationPrompt.MAP);
            case HUNTING_GROUNDS -> CreationPrompt.choose(
                crewStep,
                "Where do you usually work? Hunting grounds are the streets you claim — pick a district, then a beat.",
                CreationCatalog.districtOptions(),
                List.of(PromptField.text(
                    "detail",
                    "Your beat",
                    "Which streets, docks, or clientele you squeeze.",
                    true)),
                CreationPrompt.MAP);
            case ABILITY -> CreationPrompt.choose(
                crewStep,
                "Choose one crew special ability.",
                crew.type() == null ? List.of() : CreationCatalog.abilityOptions(crew.type().getAbilities()));
            case CONTACT -> CreationPrompt.choose(
                crewStep,
                "Choose a crew contact.",
                crew.type() == null ? List.of() : CreationCatalog.contactOptions(crew.type().getContacts()));
            case UPGRADES -> CreationPrompt.choose(
                crewStep,
                "Choose extra upgrades (" + extraUpgradesPicked + " of " + EXTRA_UPGRADES
                    + "). Starting upgrades are already on the sheet.",
                CreationCatalog.upgradeOptions(unownedUpgrades()));
            case NAME -> CreationPrompt.choose(
                crewStep,
                "Name the crew.",
                List.of(),
                List.of(PromptField.text("name", "Crew name", "What the city will learn to fear.", true)),
                CreationPrompt.NONE);
            case DONE -> CreationPrompt.done("Crew sheet is complete.");
        };
    }

    private List<Upgrade> unownedUpgrades() {
        List<Upgrade> pool = CreationCatalog.upgradePool(crew.type());
        List<Upgrade> open = new ArrayList<>();
        for (Upgrade upgrade : pool) {
            boolean owned = false;
            for (Upgrade have : crew.upgrades()) {
                if (have.getName().equalsIgnoreCase(upgrade.getName())) {
                    owned = true;
                    break;
                }
            }
            if (!owned) {
                open.add(upgrade);
            }
        }
        return open;
    }

    @Override
    public SessionZeroStatus getSessionZeroStatus() {
        return new SessionZeroStatus(
            crewStep,
            joiningClosed,
            List.copyOf(joinedPcIds),
            List.copyOf(readyPcIds),
            extraUpgradesPicked
        );
    }

    @Override
    public String getWorldBrief() {
        return worldBrief == null ? "" : worldBrief;
    }

    @Override
    public List<ScoreOpportunity> getOpportunities() {
        return List.copyOf(opportunities);
    }

    @Override
    public String getLastInvestigation() {
        return lastInvestigation == null ? "" : lastInvestigation;
    }

    @Override
    public String investigate(String question) {
        if (phase != Phase.FREEPLAY && phase != Phase.SESSION_ZERO) {
            throw new IllegalStateException("Investigate during free play (phase=" + phase + ")");
        }
        String focus = question == null || question.isBlank() ? "What trouble is moving on our turf?" : question.trim();
        lastInvestigation = WorkflowSupport.llmActivities().narrate(
            worldBrief + "\n" + crewSummary(),
            "The crew investigates: " + focus
        );
        return lastInvestigation;
    }

    @Override
    public void startScore(ScoreRequest request) {
        if (phase == Phase.SESSION_ZERO || !sessionZeroComplete) {
            return;
        }
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
