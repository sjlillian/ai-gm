package aigm.starters;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import aigm.workflow.GameWorkflow;

/* A starter is the API that connects the outside (Discord, Webapp, etc.) to the workflow. This should be a list of endpoints in my application that are called by the client */
public class GameStarter {
    public void run() {

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue("GAME_TASK_QUEUE")
                .build();

        GameWorkflow workflow = client.newWorkflowStub(GameWorkflow.class, options);

        workflow.runGameSession();
    }
}