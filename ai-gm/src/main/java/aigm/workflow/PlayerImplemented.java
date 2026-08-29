package aigm.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aigm.gamestate.Ability;
import aigm.gamestate.Clock;
import aigm.gamestate.Contact;
import aigm.gamestate.DiceRoll;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Advancement;
import aigm.gamestate.player.Background;
import aigm.gamestate.player.Harm;
import aigm.gamestate.player.Heritage;
import aigm.gamestate.player.PlaybookEnum;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import aigm.gamestate.player.Vice;
import aigm.gamestate.player.ViceKind;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInfo;

/**
 * Long-lived PC sheet owner. Incomplete sheets run character creation first,
 * then continueAsNew into the signal loop.
 */
public class PlayerImplemented implements PlayerWorkflow {

    private Player player;
    private final Map<String, Clock> projects = new HashMap<>();
    private final List<String> personalAssets = new ArrayList<>();
    private boolean needsTraumaChoice;
    private boolean ended;
    private int signalCount;
    private PcCreationStep creationStep = PcCreationStep.DONE;
    private String joinId;

    @Override
    public void run(Player player) {
        this.player = player;
        this.joinId = player == null ? "" : player.name();
        if (this.player == null || this.player.playbook() == null) {
            characterCreation();
            if (ended) {
                return;
            }
            signalCampaignReady();
            Workflow.continueAsNew(this.player);
            return;
        }
        while (!ended) {
            int seen = signalCount;
            Workflow.await(() -> ended || signalCount != seen);
            if (ended) {
                return;
            }
            if (Workflow.getInfo().isContinueAsNewSuggested()) {
                Workflow.continueAsNew(this.player);
            }
        }
    }

    private void characterCreation() {
        creationStep = PcCreationStep.PLAYBOOK;
        Workflow.await(() -> ended || (player != null && player.isCreationComplete()));
        creationStep = PcCreationStep.DONE;
    }

    private void signalCampaignReady() {
        WorkflowInfo info = Workflow.getInfo();
        String parentId = info.getParentWorkflowId().orElse("");
        if (parentId.isBlank()) {
            return;
        }
        String pcId = joinId == null || joinId.isBlank() ? player.name() : joinId;
        CampaignWorkflow campaign = Workflow.newExternalWorkflowStub(CampaignWorkflow.class, parentId);
        campaign.pcCreationComplete(pcId, player);
    }

    private void requireStep(PcCreationStep expected) {
        if (player != null && player.isCreationComplete()) {
            throw new IllegalStateException("Character creation is already complete");
        }
        if (creationStep != expected) {
            throw new IllegalStateException("Expected " + expected + ", currently " + creationStep);
        }
    }

    @Override
    public CreationPrompt choosePlaybook(String playbookName) {
        requireStep(PcCreationStep.PLAYBOOK);
        PlaybookEnum playbook = CreationCatalog.playbook(playbookName);
        player = player
            .withPlaybook(playbook)
            .withActionRatings(new HashMap<>(playbook.getStartingActionRatings()))
            .withAbilities(List.of());
        creationStep = PcCreationStep.HERITAGE;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt chooseHeritage(Heritage heritage, String detail) {
        requireStep(PcCreationStep.HERITAGE);
        if (heritage == null) {
            throw new IllegalArgumentException("heritage required");
        }
        if (detail == null || detail.isBlank()) {
            throw new IllegalArgumentException("heritage detail required"
                + (heritage == Heritage.TYCHEROS ? " (include a demonic telltale)" : ""));
        }
        player = player.withHeritage(heritage, detail.trim());
        creationStep = PcCreationStep.BACKGROUND;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt chooseBackground(Background background, String detail) {
        requireStep(PcCreationStep.BACKGROUND);
        if (background == null) {
            throw new IllegalArgumentException("background required");
        }
        if (detail == null || detail.isBlank()) {
            throw new IllegalArgumentException("background detail required");
        }
        player = player.withBackground(background, detail.trim());
        creationStep = PcCreationStep.ACTIONS;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt assignActionDot(Action action) {
        requireStep(PcCreationStep.ACTIONS);
        if (action == null) {
            throw new IllegalArgumentException("action required");
        }
        int current = player.getActionRating(action);
        if (current >= Player.CREATION_ACTION_RATING_CAP) {
            throw new IllegalArgumentException("Cannot raise " + action + " above 2 during creation");
        }
        if (player.extraActionDots() >= Player.CREATION_EXTRA_DOTS) {
            throw new IllegalStateException("All four extra action dots are already spent");
        }
        player = player.withActionRating(action, current + 1);
        if (player.extraActionDots() >= Player.CREATION_EXTRA_DOTS) {
            creationStep = PcCreationStep.ABILITY;
        }
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt chooseAbility(String abilityName) {
        requireStep(PcCreationStep.ABILITY);
        if (player.playbook() == null) {
            throw new IllegalStateException("Choose a playbook first");
        }
        Ability ability = CreationCatalog.ability(player.playbook().getAvailableAbilities(), abilityName);
        player = player.withAbilities(List.of(ability));
        creationStep = PcCreationStep.CONTACTS;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt chooseContacts(String friendName, String rivalName) {
        requireStep(PcCreationStep.CONTACTS);
        if (player.playbook() == null) {
            throw new IllegalStateException("Choose a playbook first");
        }
        List<Contact> pool = player.playbook().getAvailableContacts();
        Contact friend = CreationCatalog.contact(pool, friendName);
        Contact rival = CreationCatalog.contact(pool, rivalName);
        if (friend.equals(rival)) {
            throw new IllegalArgumentException("Friend and rival must be different contacts");
        }
        player = player.withFriend(friend).withRival(rival);
        creationStep = PcCreationStep.VICE;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt chooseVice(ViceKind kind, String purveyor) {
        requireStep(PcCreationStep.VICE);
        if (kind == null) {
            throw new IllegalArgumentException("vice required");
        }
        if (purveyor == null || purveyor.isBlank()) {
            throw new IllegalArgumentException("vice purveyor required");
        }
        player = player.withVice(new Vice(kind, purveyor.trim()));
        creationStep = PcCreationStep.IDENTITY;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt setIdentity(String name, String alias, String look) {
        requireStep(PcCreationStep.IDENTITY);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name required");
        }
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("alias required");
        }
        if (look == null || look.isBlank()) {
            throw new IllegalArgumentException("look required");
        }
        player = player.withIdentity(name.trim(), alias.trim(), look.trim());
        creationStep = PcCreationStep.DONE;
        return getCreationPrompt();
    }

    @Override
    public CreationPrompt getCreationPrompt() {
        if (player != null && player.isCreationComplete()) {
            return CreationPrompt.done(player.name() + " is ready.");
        }
        return switch (creationStep) {
            case PLAYBOOK -> CreationPrompt.of(
                creationStep,
                "Choose a playbook.",
                CreationCatalog.playbookNames());
            case HERITAGE -> CreationPrompt.of(
                creationStep,
                "Choose a heritage and a detail"
                    + (player.heritage() == Heritage.TYCHEROS
                        ? ""
                        : " (Tycheros needs a demonic telltale)."),
                CreationCatalog.heritageNames());
            case BACKGROUND -> CreationPrompt.of(
                creationStep,
                "Choose a background and a detail.",
                CreationCatalog.backgroundNames());
            case ACTIONS -> CreationPrompt.of(
                creationStep,
                "Assign extra action dots one at a time (max rating 2). "
                    + player.extraActionDots() + " of " + Player.CREATION_EXTRA_DOTS + " spent.",
                CreationCatalog.actionNames());
            case ABILITY -> CreationPrompt.of(
                creationStep,
                "Choose one special ability.",
                CreationCatalog.playbookAbilityNames(player.playbook()));
            case CONTACTS -> CreationPrompt.of(
                creationStep,
                "Choose a friend and a rival from the playbook list.",
                CreationCatalog.playbookContactNames(player.playbook()));
            case VICE -> CreationPrompt.of(
                creationStep,
                "Choose a vice and a purveyor (person or place).",
                CreationCatalog.viceNames());
            case IDENTITY -> CreationPrompt.of(
                creationStep,
                "Set name, alias, and look.",
                List.of());
            case DONE -> CreationPrompt.done("Character creation complete.");
        };
    }

    @Override
    public boolean isCreationComplete() {
        return player != null && player.isCreationComplete();
    }

    @Override
    public PcCreationStep getCreationStep() {
        return creationStep;
    }

    private void noteSignal() {
        signalCount++;
    }

    @Override
    public void markStress(int amount) {
        if (ended || amount == 0) {
            return;
        }
        noteSignal();
        boolean overflow = player.trauma().stressOverflows(amount);
        player = player.withTrauma(player.trauma().updateStress(amount));
        if (overflow) {
            needsTraumaChoice = true;
        }
    }

    @Override
    public void markTrauma(Trauma.Condition condition) {
        noteSignal();
        player = player.withTrauma(player.trauma().withTrauma(condition));
        needsTraumaChoice = false;
        if (player.trauma().isRetired()) {
            ended = true;
        }
    }

    @Override
    public void takeHarm(String description, Harm.HarmLevel level, boolean armorMarked) {
        noteSignal();
        player = player.withHarm(player.harm().withInjury(description, level, armorMarked));
        if (player.harm().isDead()) {
            ended = true;
        }
    }

    @Override
    public void markXp(Advancement.XpTrack track, int amount) {
        noteSignal();
        player = player.withAdvancement(player.advancement().mark(track, amount));
    }

    @Override
    public void resolveVice(DiceRoll viceRoll) {
        noteSignal();
        int clear = viceRoll.highest();
        boolean overindulge = clear > player.trauma().stress().progress();
        player = player.withTrauma(player.trauma().updateStress(-clear));
        if (overindulge) {
            Workflow.getLogger(PlayerImplemented.class)
                .info("Overindulgence for {}", player.name());
        }
    }

    @Override
    public void applyRecovery(int segments, Harm.RecoveryChoice choice) {
        noteSignal();
        player = player.withHarm(player.harm().applyRecovery(segments, choice));
    }

    @Override
    public void applyProjectProgress(String clockName, int segments) {
        noteSignal();
        Clock clock = projects.get(clockName);
        if (clock == null) {
            return;
        }
        projects.put(clockName, clock.tick(segments));
    }

    @Override
    public void startProject(String name, int segments) {
        noteSignal();
        if (projects.containsKey(name)) {
            return;
        }
        projects.put(name, new Clock(name, 0, Math.max(1, segments)));
    }

    @Override
    public void addPersonalAsset(String asset) {
        noteSignal();
        personalAssets.add(asset);
    }

    @Override
    public void endCharacter() {
        ended = true;
        noteSignal();
    }

    @Override
    public Player getState() {
        return player;
    }

    @Override
    public Map<String, Clock> getProjects() {
        return Map.copyOf(projects);
    }

    @Override
    public List<String> getPersonalAssets() {
        return List.copyOf(personalAssets);
    }

    @Override
    public boolean needsTraumaChoice() {
        return needsTraumaChoice;
    }
}
