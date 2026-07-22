package aigm.activities;

import aigm.gamestate.enums.Action;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GameActivities {

    /*
    A session is like an episode of a TV show.

    Freeplay- characters talk to each other, they go and do things, they make rolls as needed
    Score- Choose target, plan, engagement, and start score medi res
    Downtime- end of score, the payoff and entanglements
    */

    @ActivityMethod
    void handleAction(Action action);

}