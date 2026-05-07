package aigm;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import aigm.activities.GameActivitiesImpl;
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

        // Start the workflow
        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("GAME_TASK_QUEUE")
                .build();

        GameWorkflow workflow = client.newWorkflowStub(GameWorkflow.class, options);
        workflow.runGameSession();

        System.out.println("Game session completed.");
    }
}
