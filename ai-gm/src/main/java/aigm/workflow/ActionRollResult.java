package aigm.workflow;

import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.player.Action;

public record ActionRollResult(
    String pcId,
    Action action,
    Position position,
    Effect effect,
    int diceRolled,
    int highestDie,
    int sixes,
    boolean pushed,
    boolean assisted,
    String consequence
) {

    public boolean isCritical() {
        return sixes >= 2;
    }

    public boolean isFullSuccess() {
        return highestDie == 6;
    }

    public boolean isPartialSuccess() {
        return highestDie == 4 || highestDie == 5;
    }

    public boolean isBadOutcome() {
        return highestDie <= 3;
    }
}
