package aigm.gamestate.player;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import aigm.gamestate.Clock;

/**
 * Stress is 9 boxes. Filling it marks one trauma (player's choice), clears stress,
 * and takes the PC out of the current scene. Four trauma conditions retires the PC.
 */
public record Trauma(
    Clock stress,
    List<Condition> conditions
) {

    public static final int STRESS_BOXES = 9;
    public static final int TRAUMA_LIMIT = 4;

    public enum Condition {
        COLD("You're not moved by emotional appeals or social bonds."),
        HAUNTED("You're often lost in reverie, reliving past horrors, seeing things."),
        OBSESSED("You're enthralled by one thing: an activity, a person, an ideology."),
        PARANOID("You imagine danger everywhere; you can't trust others."),
        RECKLESS("You have little regard for your own safety or best interests."),
        SOFT("You lose your edge; you become sentimental, passive, gentle."),
        UNSTABLE("Your emotional state is volatile. You can instantly rage, or fall into despair, act impulsively, or freeze up."),
        VICIOUS("You seek out opportunities to hurt people, even for no good reason.");

        private final String description;

        Condition(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public Trauma {
        conditions = List.copyOf(conditions);
    }

    public Trauma() {
        this(new Clock("Stress", STRESS_BOXES), List.of());
    }

    @JsonIgnore
    public boolean isRetired() {
        return conditions.size() >= TRAUMA_LIMIT;
    }

    public boolean stressOverflows(int delta) {
        return stress.progress() + delta >= stress.max();
    }

    /** Mark or clear stress. Does not choose trauma; call {@link #withTrauma(Condition)} after overflow. */
    public Trauma updateStress(int delta) {
        if (isRetired()) {
            return this;
        }
        if (stressOverflows(delta)) {
            return new Trauma(new Clock("Stress", STRESS_BOXES), conditions);
        }
        return new Trauma(stress.tick(delta), conditions);
    }

    public Trauma withTrauma(Condition condition) {
        if (isRetired() || conditions.contains(condition)) {
            return this;
        }
        List<Condition> updated = new ArrayList<>(conditions);
        updated.add(condition);
        return new Trauma(new Clock("Stress", STRESS_BOXES), List.copyOf(updated));
    }

    public Trauma withStressClock(Clock stress) {
        return new Trauma(stress, conditions);
    }

    public Trauma withConditions(List<Condition> conditions) {
        return new Trauma(stress, conditions);
    }
}
