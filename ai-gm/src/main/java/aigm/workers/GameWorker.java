package aigm.workers;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import aigm.activities.GameActivitiesImpl;
import aigm.workflows.GameWorkflowImpl;

public class GameWorker {
    public static void main(String[] args) {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker("GAME_TASK_QUEUE");

        worker.registerWorkflowImplementationTypes(GameWorkflowImpl.class);
        worker.registerActivitiesImplementations(new GameActivitiesImpl());

        factory.start();

        System.out.println("Worker started.");
    }
}