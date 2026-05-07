package aigm.gamestate;

import java.io.Serializable;

public record TurnResult(GameState state, String narration) implements Serializable {
}