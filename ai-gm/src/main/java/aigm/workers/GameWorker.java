package aigm.workers;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;
import io.temporal.worker.tuning.PollerBehaviorAutoscaling;
import aigm.activities.GameActivitiesImpl;
import aigm.workflow.GameWorkflowImpl;

/* A worker is a long running process that handles the execution of workflows and activities. It is the bridge between the Temporal service and the actual logic of the application */

public class GameWorker {
    public static void main(String[] args) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);

        WorkerOptions options = WorkerOptions.newBuilder()
                .setWorkflowTaskPollersBehavior(new PollerBehaviorAutoscaling())
                .setActivityTaskPollersBehavior(new PollerBehaviorAutoscaling())
                .setNexusTaskPollersBehavior(new PollerBehaviorAutoscaling())
                .build();

        Worker worker = factory.newWorker("GAME_TASK_QUEUE", options);

        worker.registerWorkflowImplementationTypes(GameWorkflowImpl.class);
        worker.registerActivitiesImplementations(new GameActivitiesImpl());

        factory.start();

        System.out.println("Worker started.");
    }
}