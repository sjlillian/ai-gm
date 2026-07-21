package aigm.gamestate.score;

import java.util.ArrayList;
import java.util.List;
import aigm.gamestate.Clock;

public class ScoreRuntime {

    private enum Position {
        CONTROLLED("You have a golden opportunity. You're exploiting a dominant advantage. You're set up for success."),
        RISKY("You go head to head. You're acting under duress. You're taking a chance."),
        DESPERATE("You're in serious trouble. You're overreaching your capabilities. You're attempting a dangerous maneuver.");

        private String description;

        Position(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private enum Effect {
        GREAT("You achieve more than usual. How does the extra effort manifest? What additional benefit do you enjoy?", 3),
        STANDARD("You achieve what we'd expect as \"normal\" with this action. Is that enough, or is there more left to do?", 2),
        LIMITED("You achieve a partial or weak effect. How is your impact diminished? What effor remains to achieve your goal?", 1);

        private String description;
        private int ticks;
        Effect(String description, int ticks) {
            this.description = description;
            this.ticks = ticks;
        }

        public String getDescription() {
            return description;
        }

        public int getTicks() {
            return ticks;
        }
    }

    private List<Clock> clocks;
    private Position position;
    private Effect effect;

    public ScoreRuntime() {
        this.clocks = new ArrayList<>();
        this.position = Position.RISKY;
        this.effect = Effect.STANDARD;
    }

    public ScoreRuntime(List<Clock> clocks, Position position, Effect effect) {
        this.clocks = clocks;
        this.position = position;
        this.effect = effect;
    }

    public List<Clock> getClocks() {
        return clocks;
    }

    public Position getPosition() {
        return position;
    }

    public Effect getEffect() {
        return effect;
    }
}
