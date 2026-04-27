package aigm.gamestate;

import java.io.Serializable;

public class TurnResult implements Serializable {
    private GameState state;
    private String narration;

    public TurnResult(GameState state, String narration) {
        this.state = state;
        this.narration = narration;
    }

    public GameState getState() {
        return state;
    }

    public String getNarration() {
        return narration;
    }
}