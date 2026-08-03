package aigm.gamestate.player;

import java.util.Arrays;

public enum Attribute {
    INSIGHT,
    PROWESS,
    RESOLVE;

    public int calculateRating(Player player) {
        return (int) Arrays.stream(Action.values())
            .filter(a -> a.getAttribute() == this)
            .filter(a -> player.actionRatings().getOrDefault(a, 0) > 0)
            .count();
    }
}
