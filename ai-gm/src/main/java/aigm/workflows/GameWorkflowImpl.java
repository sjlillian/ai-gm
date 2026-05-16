package aigm.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

import aigm.activities.GameActivities;
import aigm.gamestate.GameState;
import aigm.gamestate.TurnResult;

public class GameWorkflowImpl implements GameWorkflow {

    @Override
    public void runGameSession(GameState initialState) {

        GameState state = initialState;

        GameActivities activities = Workflow.newActivityStub(
            GameActivities.class,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .build()
        );

        Workflow.getLogger(GameWorkflowImpl.class)
            .info("You stand in a dim alley. A Bluecoat guard blocks the exit, lantern in hand.");

        Workflow.getLogger(GameWorkflowImpl.class)
            .info("What do you do?");

        for (int i = 0; i < 3; i++) {
            
            TurnResult result = activities.handleTurn(state);

            Workflow.getLogger(GameWorkflowImpl.class)
                .info(result.narration() + " | State: " + result.state().toString());
        }
    }
}