package aigm;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.util.ArrayList;
import java.util.List;

import aigm.activities.GameActivitiesImpl;
import aigm.gamestate.GameState;
import aigm.gamestate.Player;
import aigm.workflows.GameWorkflow;
import aigm.workflows.GameWorkflowImpl;

/**
 * Main application entry point that starts Temporal worker and runs the game session.
 */
public class App {
    public static void main(String[] args) {
        // Create Temporal service stubs (assumes Temporal server is running locally)
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Start the worker
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker("GAME_TASK_QUEUE");
        worker.registerWorkflowImplementationTypes(GameWorkflowImpl.class);
        worker.registerActivitiesImplementations(new GameActivitiesImpl());
        factory.start();

        System.out.println("Temporal worker started. Starting game session...");

        // Create initial game state with players (can be passed in from external source)
        GameState initialState = createInitialGameState();

        // Start the workflow
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("GAME_TASK_QUEUE")
                .build();

        GameWorkflow workflow = client.newWorkflowStub(GameWorkflow.class, options);
        workflow.runGameSession(initialState);

        System.out.println("Game session completed.");
    }

    /**
     * Creates the initial game state with players.
     * This method can be replaced with external data loading if needed.
     */
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
