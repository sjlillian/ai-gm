package aigm.gamestate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aigm.gamestate.enums.Phase;

public record GameState(
        Phase phase,
        Map<String, Integer> playerStress,
        List<Clock> clocks
) implements Serializable {

    public GameState() {
        this(Phase.FREEPLAY, new HashMap<>(), new ArrayList<>());
    }
}