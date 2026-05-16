package aigm.workflows;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import aigm.gamestate.GameState;

@WorkflowInterface
public interface GameWorkflow {
    @WorkflowMethod
    void runGameSession(GameState initialState);
}