package aigm.gamestate;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.List;

import aigm.gamestate.enums.Phase;

@WorkflowInterface
public interface GameWorkflow {

    // Getters
    String getGameId();

    List<Player> getPlayers();

    List<Crew> getCrews();

    List<Clock> getClocks();

    Phase getPhase();

    // Setters
    void setGameId(String gameId);

    void setPlayers(List<Player> players);

    void setCrews(List<Crew> crews);

    void setClocks(List<Clock> clocks);

    void setPhase(Phase phase);

    @WorkflowMethod
    void runGameSession();
}