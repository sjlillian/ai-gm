package aigm.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import aigm.activities.GameActivities;
import aigm.gamestate.Clock;
import aigm.gamestate.Crew;
import aigm.gamestate.Player;
import aigm.gamestate.enums.Action;
import aigm.gamestate.enums.Phase;

public class GameWorkflowImpl implements GameWorkflow {

    // Game State information
    String gameId;
    List<Player> players;
    List<Crew> crews;
    List<Clock> clocks;
    Phase phase;

    public GameWorkflowImpl() {
        this("0", new ArrayList<Player>(), new ArrayList<Crew>(), new ArrayList<Clock>(), Phase.FREEPLAY);
    }

    public GameWorkflowImpl(String gameId, List<Player> players, List<Crew> crews, List<Clock> clocks, Phase phase) {
        this.gameId = gameId;
        this.players = players;
        this.crews = crews;
        this.clocks = clocks;
        this.phase = phase;
    }

    // Getters
    public String getGameId() {
        return gameId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Crew> getCrews() {
        return crews;
    }

    public List<Clock> getClocks() {
        return clocks;
    }

    public Phase getPhase() {
        return phase;
    }

    // Setters
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setCrews(List<Crew> crews) {
        this.crews = crews;
    }

    public void setClocks(List<Clock> clocks) {
        this.clocks = clocks;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    @Override
    public void runGameSession() {

        GameActivities activities = Workflow.newActivityStub(
                GameActivities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofMinutes(5))
                        .build());

        Workflow.getLogger(GameWorkflowImpl.class)
                .info("You stand in a dim alley. A Bluecoat guard blocks the exit, lantern in hand.");

        Workflow.getLogger(GameWorkflowImpl.class)
                .info("What do you do?");

        for (int i = 0; i < 3; i++) {

            activities.handleAction(Action.SKIRMISH);

            // Workflow.getLogger(GameWorkflowImpl.class)
            // .info(result.narration() + " | State: " + result.state().toString());
        }
    }
}