package aigm.gamestate.player;

import java.util.List;
import java.util.Map;

import aigm.gamestate.Ability;
import aigm.gamestate.Contact;

public record PlaybookCustom(
    String name,
    Map<Action, Integer> startingActionRatings,
    List<Ability> availableAbilities,
    List<Item> availableItems,
    List<Contact> availableContacts,
    List<String> xpTriggers
) implements Playbook {

    public PlaybookCustom {
        startingActionRatings = Map.copyOf(startingActionRatings);
        availableAbilities = List.copyOf(availableAbilities);
        availableItems = List.copyOf(availableItems);
        availableContacts = List.copyOf(availableContacts);
        xpTriggers = List.copyOf(xpTriggers);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<Action, Integer> getStartingActionRatings() {
        return startingActionRatings;
    }

    @Override
    public List<Ability> getAvailableAbilities() {
        return availableAbilities;
    }

    @Override
    public List<Item> getAvailableItems() {
        return availableItems;
    }

    @Override
    public List<Contact> getAvailableContacts() {
        return availableContacts;
    }

    @Override
    public List<String> getXpTriggers() {
        return xpTriggers;
    }

}
