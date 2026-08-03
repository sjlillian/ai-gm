package aigm.gamestate.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import aigm.gamestate.Clock;

public record Harm(
    List<Injury> injuries,
    Clock recoveryClock,
    boolean isDead
) {

    public enum HarmLevel {
        LESSER(0), MODERATE(1), SEVERE(2), CATASTROPHIC(3); // levels for indexing

        private final int level;

        HarmLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
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

    private static final Clock DEFAULT_RECOVERY_CLOCK = new Clock("Recovery", 0, 6);

    private static final Map<HarmLevel, Integer> CAPACITY = Map.of(
        HarmLevel.LESSER, 2,
        HarmLevel.MODERATE, 2,
        HarmLevel.SEVERE, 1,
        HarmLevel.CATASTROPHIC, 0
    );

    public Harm() {
        this(new ArrayList<>(), DEFAULT_RECOVERY_CLOCK, false);
    }

    public Harm withInjury(String description, HarmLevel level) {
        if (isDead) return this; // No changes if already dead

        int occupied = atLevel(level).size();
        if (occupied < CAPACITY.get(level)) {
            List<Injury> newInjuries = new ArrayList<>(injuries);
            newInjuries.add(new Injury(description, level));
            return new Harm(newInjuries, recoveryClock, isDead);
        }

        if (level == HarmLevel.CATASTROPHIC) {
            return new Harm(injuries, recoveryClock, true); // Player is dead
        }

        return withInjury(description, level.upgrade()); // Try next level up
    }

    public Harm withInjury(String description, HarmLevel level, Armor armor) {
        HarmLevel effectiveLevel = applyArmor(level, armor);

        if (effectiveLevel == null) {
            return this; // Armor negates the injury
        }
        return withInjury(description, effectiveLevel);
    }

    private HarmLevel applyArmor(HarmLevel level, Armor armor) {
        if (armor == null) return level;

        switch (armor) {
            case STANDARD:
                if (level == HarmLevel.LESSER) return null; // Negated
                return level.downgrade();
            case HEAVY:
                if (level == HarmLevel.LESSER || level == HarmLevel.MODERATE) return null; // Negated
                return level.downgrade();
            case SPECIAL:
                return null; // STUB: All injuries negated TODO: Implement special armor logic based on playbook abilities
            default:
                return level;
        }
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

    public Harm applyRecovery(int delta) {
        if (isDead) return this; // No changes if already dead

        Clock updatedrecoveryClock = recoveryClock.tick(delta);
        if (updatedrecoveryClock.isComplete()) {
            return new Harm(new ArrayList<>(), updatedrecoveryClock, isDead); // All injuries healed
        }
        return this;
    }
}
