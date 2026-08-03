package aigm.gamestate.player;

import java.util.List;
import java.util.Map;

import aigm.gamestate.Ability;

public interface Playbook {

    String getName();
    Map<Action, Integer> getStartingActionRatings();
    List<Ability> getAvailableAbilities();
    List<Item> getAvailableItems();
    List<String> getXpTriggers();

}
