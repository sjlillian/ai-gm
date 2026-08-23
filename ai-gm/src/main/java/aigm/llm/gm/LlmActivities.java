package aigm.llm.gm;

import java.util.List;

import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.player.Action;
import aigm.llm.LlmRequest;
import aigm.llm.LlmResponse;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal activities that call an {@link aigm.llm.LlmClient}. Register
 * {@link LlmActivitiesImpl} on the worker next to dice {@code ActivitiesImpl}.
 */
@ActivityInterface
public interface LlmActivities {

    @ActivityMethod
    Adjudication adjudicateAction(String situation, String approach, Action chosenAction);

    @ActivityMethod
    String narrate(String situation, String mechanicalOutcome);

    /** Escape hatch: send any prompt through the configured client. */
    @ActivityMethod
    LlmResponse complete(LlmRequest request);

    record Adjudication(
        Action action,
        Position position,
        Effect effect,
        String reasoning,
        List<String> possibleStakes
    ) {
        public Adjudication {
            possibleStakes = possibleStakes == null ? List.of() : List.copyOf(possibleStakes);
            reasoning = reasoning == null ? "" : reasoning;
        }
    }
}
