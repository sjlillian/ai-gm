package aigm.activities;

import io.temporal.activity.ActivityInterface;
import aigm.gamestate.GameState;
import aigm.gamestate.TurnResult;

@ActivityInterface
public interface GameActivities {
    TurnResult handleTurn(String input, GameState state);

    String getPlayerInput();
}