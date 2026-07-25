package aigm.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface GameWorkflow {
    @WorkflowMethod
    void runCampaign();
}