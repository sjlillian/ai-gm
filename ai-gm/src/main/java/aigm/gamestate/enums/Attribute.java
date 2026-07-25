package aigm.gamestate.enums;

import java.util.Arrays;

import aigm.gamestate.player.Player;

public enum Attribute {
    INSIGHT,
    PROWESS,
    RESOLVE;

    public int calculateRating(Player player) {
        return (int) Arrays.stream(Action.values())
            .filter(a -> a.getAttribute() == this)
            .filter(a -> player.getActionRating(a) > 0)
            .count();
    }
}
