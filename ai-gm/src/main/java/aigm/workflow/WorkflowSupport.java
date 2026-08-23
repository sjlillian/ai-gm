package aigm.workflow;

import java.time.Duration;

import aigm.activities.Activities;
import aigm.gamestate.DiceRoll;
import aigm.gamestate.Position;
import aigm.llm.gm.LlmActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

final class WorkflowSupport {

    static final String TASK_QUEUE = "GAME_TASK_QUEUE";

    private WorkflowSupport() {}

    static Activities activities() {
        return Workflow.newActivityStub(
            Activities.class,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofSeconds(60))
                .setRetryOptions(
                    RetryOptions.newBuilder()
                        .setMaximumAttempts(3)
                        .build()
                )
                .build()
        );
    }

    static LlmActivities llmActivities() {
        return Workflow.newActivityStub(
            LlmActivities.class,
            ActivityOptions.newBuilder()
                .setStartToCloseTimeout(Duration.ofMinutes(10))
                .setRetryOptions(
                    RetryOptions.newBuilder()
                        .setMaximumAttempts(3)
                        .setInitialInterval(Duration.ofSeconds(2))
                        .setBackoffCoefficient(2.0)
                        .build()
                )
                .build()
        );
    }

    static String pcWorkflowId(String campaignWorkflowId, String pcId) {
        return "pc-" + campaignWorkflowId + "-" + pcId;
    }

    static Position engagementPosition(DiceRoll roll) {
        if (roll.isCritical() || roll.isFullSuccess()) {
            return Position.CONTROLLED;
        }
        if (roll.isPartialSuccess()) {
            return Position.RISKY;
        }
        return Position.DESPERATE;
    }
}
