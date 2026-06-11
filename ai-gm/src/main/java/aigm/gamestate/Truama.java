package aigm.gamestate;

import java.util.ArrayList;
import java.util.List;

public class Truama {

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
    
    private Clock stress = new Clock("Stress", 9);
    private List<Condition> conditions = new ArrayList<>();

    public void addStress(int segments) {
        
        while (segments > 0) {
            stress.tick();
            if (stress.isComplete()) {
                stress.setProgress(0); // Reset stress clock
                addCondition(); // Add a new condition
            }
            segments--;
        }
    }

    private void addCondition() {
        // For simplicity, we'll just add conditions in a fixed order.
        // In a real implementation, you might want to randomize this or allow player choice.
        Condition[] allConditions = Condition.values();
        for (Condition condition : allConditions) {
            if (!conditions.contains(condition)) {
                conditions.add(condition);
                break;
            }
        }
    }

    public Clock getStress() {
        return stress;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

}
