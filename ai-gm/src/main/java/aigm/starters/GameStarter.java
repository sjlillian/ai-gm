package aigm.starters;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.ArrayList;
import java.util.List;

import aigm.gamestate.GameState;
import aigm.gamestate.Player;
import aigm.workflows.GameWorkflow;

public class GameStarter {
    public static void main(String[] args) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("GAME_TASK_QUEUE")
                .build();

        GameWorkflow workflow =
                client.newWorkflowStub(GameWorkflow.class, options);

        GameState initialState = createInitialGameState();
        
        workflow.runGameSession(initialState);
    }

    private static GameState createInitialGameState() {
        // Create a test player
        Player testPlayer = new Player("player");
        
        // Create the initial game state
        GameState initialState = new GameState();
        
        // Add the player to the state
        List<Player> players = new ArrayList<>(initialState.players());
        players.add(testPlayer);
        
        // Return a new GameState with the player
        return new GameState(
            initialState.phase(),
            initialState.crews(),
            players,
            initialState.clocks()
        );
    }
}