package aigm.activities;

import aigm.gamestate.enums.Action;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GameActivities {

    @ActivityMethod
    void handleAction(Action action);

}