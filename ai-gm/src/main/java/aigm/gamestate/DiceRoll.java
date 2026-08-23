package aigm.gamestate;

import java.util.List;

/** Result of a d6 pool. Zero-dice pools roll two dice and keep the lowest. */
public record DiceRoll(List<Integer> dice, int highest, int lowest, int sixes, boolean zeroDice) {

    public DiceRoll {
        dice = List.copyOf(dice);
    }

    public boolean isCritical() {
        return sixes >= 2;
    }

    public boolean isFullSuccess() {
        return highest == 6;
    }

    public boolean isPartialSuccess() {
        return highest == 4 || highest == 5;
    }

    public boolean isBadOutcome() {
        return highest <= 3;
    }
}
