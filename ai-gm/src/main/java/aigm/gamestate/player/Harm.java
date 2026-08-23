package aigm.gamestate.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import aigm.gamestate.Clock;

/**
 * Harm levels 1–4: lesser (2 slots), moderate (2), severe (1), fatal (no slot — resists or dies).
 * When a level is full, new harm at that level bumps up. Armor reduces harm by one level per mark.
 * Completing the recovery clock clears all level-1 harm or reduces every harm by one level (player choice);
 * leftover ticks carry to the next recovery clock.
 */
public record Harm(
    List<Injury> injuries,
    Clock recoveryClock,
    boolean isDead
) {

    public enum HarmLevel {
        LESSER(1),
        MODERATE(2),
        SEVERE(3),
        FATAL(4);

        private final int rulebookLevel;

        HarmLevel(int rulebookLevel) {
            this.rulebookLevel = rulebookLevel;
        }

        public int getRulebookLevel() {
            return rulebookLevel;
        }

        public HarmLevel upgrade() {
            int next = Math.min(this.ordinal() + 1, values().length - 1);
            return values()[next];
        }

        public HarmLevel downgrade() {
            int next = Math.max(this.ordinal() - 1, 0);
            return values()[next];
        }
    }

    public enum RecoveryChoice {
        CLEAR_LESSER,
        REDUCE_ALL
    }

    public record Injury(String description, HarmLevel level) {
        public Injury withLevel(HarmLevel newLevel) {
            return new Injury(description, newLevel);
        }

        public Injury withDescription(String newDescription) {
            return new Injury(newDescription, level);
        }

        public Injury upgraded() {
            return new Injury(description, level.upgrade());
        }
    }

    private static final Clock DEFAULT_RECOVERY_CLOCK = new Clock("Recovery", 0, 4);

    private static final Map<HarmLevel, Integer> CAPACITY = Map.of(
        HarmLevel.LESSER, 2,
        HarmLevel.MODERATE, 2,
        HarmLevel.SEVERE, 1,
        HarmLevel.FATAL, 0
    );

    public Harm {
        injuries = List.copyOf(injuries);
    }

    public Harm() {
        this(List.of(), DEFAULT_RECOVERY_CLOCK, false);
    }

    public Harm withInjury(String description, HarmLevel level) {
        if (isDead) {
            return this;
        }

        if (level == HarmLevel.FATAL) {
            return new Harm(injuries, recoveryClock, true);
        }

        int occupied = atLevel(level).size();
        if (occupied < CAPACITY.get(level)) {
            List<Injury> newInjuries = new ArrayList<>(injuries);
            newInjuries.add(new Injury(description, level));
            return new Harm(List.copyOf(newInjuries), recoveryClock, isDead);
        }

        return withInjury(description, level.upgrade());
    }

    /** Armor reduces harm by one level. Level-1 harm reduced this way is negated. */
    public Harm withInjury(String description, HarmLevel level, boolean armorMarked) {
        if (!armorMarked) {
            return withInjury(description, level);
        }
        if (level == HarmLevel.LESSER) {
            return this;
        }
        return withInjury(description, level.downgrade());
    }

    public List<Injury> atLevel(HarmLevel level) {
        List<Injury> filtered = new ArrayList<>();
        for (Injury injury : injuries) {
            if (injury.level().equals(level)) {
                filtered.add(injury);
            }
        }
        return filtered;
    }

    public Harm applyRecovery(int delta, RecoveryChoice choice) {
        if (isDead || injuries.isEmpty()) {
            return this;
        }

        int total = recoveryClock.progress() + delta;
        int max = recoveryClock.max();
        if (total < max) {
            return new Harm(injuries, recoveryClock.tick(delta), isDead);
        }

        Harm healed = switch (choice) {
            case CLEAR_LESSER -> clearLesser();
            case REDUCE_ALL -> reduceAllByOneLevel();
        };
        int leftover = total - max;
        Clock next = new Clock(recoveryClock.name(), leftover, max);
        if (healed.injuries().isEmpty()) {
            next = new Clock(recoveryClock.name(), 0, max);
        }
        return new Harm(healed.injuries(), next, healed.isDead());
    }

    private Harm clearLesser() {
        List<Injury> remaining = injuries.stream()
            .filter(injury -> injury.level() != HarmLevel.LESSER)
            .toList();
        return new Harm(remaining, recoveryClock, isDead);
    }

    private Harm reduceAllByOneLevel() {
        List<Injury> reduced = new ArrayList<>();
        for (Injury injury : injuries) {
            if (injury.level() == HarmLevel.LESSER) {
                continue;
            }
            reduced.add(injury.withLevel(injury.level().downgrade()));
        }
        return new Harm(List.copyOf(reduced), recoveryClock, isDead);
    }
}
