package aigm.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

import aigm.activities.GameActivities;

public class GameWorkflowImpl implements GameWorkflow {

    @Override
    public void runCampaign() {
        GameActivities activities = Workflow.newActivityStub(
                GameActivities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofMinutes(5))
                        .build());
    }
}