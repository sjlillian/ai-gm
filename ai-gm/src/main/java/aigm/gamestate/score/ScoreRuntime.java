package aigm.gamestate.score;

import java.util.ArrayList;
import java.util.List;

import aigm.gamestate.Clock;
import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import lombok.Data;

@Data
public class ScoreRuntime {

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

    public String getEffectDescription() {
        return effect.getDescription();
    }

    public int getEffectTicks() {
        return effect.getClockTicks();
    }

    public String getPositionDescription() {
        return position.getDescription();
    }
}
