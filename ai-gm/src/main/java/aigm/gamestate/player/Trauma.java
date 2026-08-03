package aigm.gamestate.player;

import java.util.List;

import aigm.gamestate.Clock;

public record Trauma(
    Clock stress,
    List<Condition> conditions
) {

    public enum Condition {
        COLD("You're not moved by emotional appeals or social bonds."),
        HAUNTED("You're often lost in reverie, reliving past horrors, seeing things."),
        OBSESSED("You're enthralled by one thing: an activity, a person, an ideology."),
        PARANOID("You imagin danger everywhere; you can't trust others."),
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
    
    public Trauma() {
        this(new Clock("Stress", 10), List.of());
    }

    public Trauma updateStress(int delta) {
        Clock updatedStress = stress.tick(delta);

        if (updatedStress.isComplete())
            return new Trauma(new Clock("Stress", 10), updateConditions()); // Player is at max stress, no further changes

        return new Trauma(updatedStress, conditions);
    }

    private List<Condition> updateConditions() {
        // Simply returns a random condition
        List<Condition> updatedConditions = new java.util.ArrayList<>(conditions);
        updatedConditions.add(Condition.values()[(int) (Math.random() * Condition.values().length)]);
        return updatedConditions;
    }

    public Trauma withStressClock(Clock stress) {
        return new Trauma(stress, conditions);
    }

    public Trauma withConditions(List<Condition> conditions) {
        return new Trauma(stress, conditions);
    }
}
