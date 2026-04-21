package aigm.starters;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

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

        workflow.runGameSession();
    }
}