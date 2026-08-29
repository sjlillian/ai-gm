package aigm.gamestate.player;

import java.util.List;
import java.util.Map;

import aigm.gamestate.Ability;
import aigm.gamestate.Contact;

public interface Playbook {

    String getName();
    Map<Action, Integer> getStartingActionRatings();
    List<Ability> getAvailableAbilities();
    List<Item> getAvailableItems();
    List<Contact> getAvailableContacts();
    List<String> getXpTriggers();

    default String getDescription() {
        return "";
    }

}
