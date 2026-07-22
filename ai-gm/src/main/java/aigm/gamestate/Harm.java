package aigm.gamestate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import aigm.gamestate.enums.Armor;

public class Harm implements Serializable {

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
            switch (this) {
                case LESSER: return MODERATE;
                case MODERATE: return SEVERE;
                case SEVERE: return CATASTROPHIC;
                default: return CATASTROPHIC; // Already at max severity
            }
        }

        public HarmLevel downgrade() {
            switch (this) {
                case CATASTROPHIC: return SEVERE;
                case SEVERE: return MODERATE;
                case MODERATE: return LESSER;
                default: return null; // Can't downgrade below Lesser
            }
        }
    }

    public class Injury {
        private String description;
        private HarmLevel level;

        public Injury(String description, HarmLevel level) {
            this.description = description;
            this.level = level;
        }

        public String getDescription() { return description; }
        public HarmLevel getLevel() { return level; }
    }

    // 3 rows of harm matching the sheet: index 0 = LESSER, index 1 = MODERATE, index 2 = SEVERE
    private final List<Injury>[] harmMatrix;
    
    // Embedded 4-segment clock specifically for recovery
    private Clock recoveryClock = new Clock("Healing", 4);
    private boolean isDead = false;

    @SuppressWarnings("unchecked")
    public Harm() {
        this.harmMatrix = new List[3];
        this.harmMatrix[0] = new ArrayList<>(2); // Lesser (Max 2)
        this.harmMatrix[1] = new ArrayList<>(2); // Moderate (Max 2)
        this.harmMatrix[2] = new ArrayList<>(1); // Severe (Max 1)
    }

    /**
     * Records new harm, automatically handling upward spillover mechanics.
     */
    public void applyHarm(String description, HarmLevel level, Armor armor) {
        if (isDead) return;

        switch (armor) {
            case STANDARD: // Standard armor negates one level of harm severity
                level = level.downgrade() != null ? level.downgrade() : level; 
                break;
            case HEAVY: // Heavy armor negates two levels of harm severity
                level = level.downgrade() != null ? level.downgrade() : level; 
                level = level.downgrade() != null ? level.downgrade() : level;
                break;
            case SPECIAL:
                // Special armor logic
                break;
            default:
                // Standard armor does not modify harm
                break;
        }

        if (level == null) return; // If armor negates all harm, do nothing

        applyHarm(description, level);
    }

    public void applyHarm(String description, HarmLevel level) {
        if (isDead) return;

        int targetRow = level.getLevel();
        if (harmMatrix[targetRow].size() < (targetRow == 2 ? 1 : 2)) {
            // If there's room in the target row, add the harm directly
            harmMatrix[targetRow].add(new Injury(description, level));
        } else {
            // Try to apply at the next severity level
            applyHarm(description, level.upgrade());
        }
    }

    /**
     * Ticks the dedicated recovery clock. Clears all harm by 1 tier when full.
     */
    public void tickRecovery(int segments) {

        while (segments > 0 && !isDead) {
            recoveryClock.tick();
            if (recoveryClock.isComplete()) {
                processHealingResolution();
                recoveryClock.setProgress(0); // Reset for next cycle
            }
            segments--;
        }
    }

    /**
     * Lowers all existing injuries by exactly 1 level. Level 1 injuries disappear.
     */
    private void processHealingResolution() {
        // Step 1: Collect and clear all existing injuries
        List<Injury> allCurrentInjuries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (Injury injury : harmMatrix[i]) {
                // Store injuries alongside their original level
                allCurrentInjuries.add(injury);
            }
            harmMatrix[i].clear();
        }

        // Step 2: Demote their level by 1 and re-apply them
        for (Injury injury : allCurrentInjuries) {
            if (injury.getLevel().downgrade() != null) {
                applyHarm(injury.getDescription(), injury.getLevel().downgrade());
            }
        }
    }

    // --- State Getters for Serialization ---
    public List<Injury>[] getHarmMatrix() { return harmMatrix; }
    public int getRecoveryClockSegments() { return recoveryClock.getProgress(); }
    public boolean isDead() { return isDead; }

    public int getDicePenalty() {
        if (!harmMatrix[2].isEmpty()) return -100; // Custom flag for Incapacitated / Can't act
        if (!harmMatrix[1].isEmpty()) return -1;   // -1 die to actions (or -1 effect depending on edition/ruling)
        return 0;
    }
}
