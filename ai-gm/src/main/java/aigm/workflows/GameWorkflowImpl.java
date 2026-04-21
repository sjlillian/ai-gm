package aigm.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

import aigm.activities.GameActivities;
import aigm.gamestate.GameState;

public class GameWorkflowImpl implements GameWorkflow {

    @Override
    public void runGameSession() {

        GameState state = new GameState();

        GameActivities activities = Workflow.newActivityStub(
            GameActivities.class,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(5))
                .build()
        );

        for (int i = 0; i < 3; i++) {

            String input = activities.getPlayerInput();

            String result = activities.handleTurn(input, state);

            Workflow.getLogger(GameWorkflowImpl.class)
                .info(result + " | Stress: " + state.getStress("player"));
        }
    }
}