package aigm.activities;

import io.temporal.activity.ActivityInterface;
import aigm.gamestate.GameState;

@ActivityInterface
public interface GameActivities {
    String handleTurn(String input, GameState state);

    String getPlayerInput();
}