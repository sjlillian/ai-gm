package aigm.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aigm.gamestate.Clock;
import aigm.gamestate.DiceRoll;
import aigm.gamestate.player.Advancement;
import aigm.gamestate.player.Harm;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import io.temporal.workflow.Workflow;

/**
 * Long-lived PC sheet owner. Mutates only via signals; continueAsNew when suggested.
 */
public class PlayerImplemented implements PlayerWorkflow {

    private Player player;
    private final Map<String, Clock> projects = new HashMap<>();
    private final List<String> personalAssets = new ArrayList<>();
    private boolean needsTraumaChoice;
    private boolean ended;
    private int signalCount;

    @Override
    public void run(Player player) {
        this.player = player;
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
