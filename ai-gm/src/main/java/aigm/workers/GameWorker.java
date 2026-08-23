package aigm.workers;

import aigm.TaskQueues;
import aigm.activities.ActivitiesImpl;
import aigm.llm.LlmClient;
import aigm.llm.LlmClients;
import aigm.llm.gm.LlmActivitiesImpl;
import aigm.workflow.CampaignImplemented;
import aigm.workflow.DowntimeImplemented;
import aigm.workflow.PlayerImplemented;
import aigm.workflow.ScoreImplemented;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerOptions;
import io.temporal.worker.tuning.PollerBehaviorAutoscaling;

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

        Worker worker = factory.newWorker(TaskQueues.GAME, options);
        worker.registerWorkflowImplementationTypes(
            CampaignImplemented.class,
            ScoreImplemented.class,
            DowntimeImplemented.class,
            PlayerImplemented.class
        );
        LlmClient llm = LlmClients.fromEnvironment();
        worker.registerActivitiesImplementations(new ActivitiesImpl(), new LlmActivitiesImpl(llm));

        factory.start();
        System.out.println("Worker started on " + TaskQueues.GAME);
        System.out.println("LLM: " + llm.describe());
    }
}
