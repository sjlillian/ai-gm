package aigm.gamestate.dto;

import java.util.List;
import java.util.Map;

import aigm.gamestate.enums.Action;
import lombok.Data;

@Data
public class Playbook {

    private String name;
    private String xpTrigger;
    private Map<Action, Integer> startingActions;
    private List<Ability> abilities;
    private List<Item> items;

    public Playbook() {
        // default constructor for serialization
    }

    public Playbook(String name, String xpTrigger, Map<Action, Integer> startingActions, List<Ability> abilities, List<Item> items) {
        this.name = name;
        this.xpTrigger = xpTrigger;
        this.startingActions = startingActions;
        this.abilities = abilities;
        this.items = items;
    }

    public void addStartingAction(Action action, Integer count) {
        this.startingActions.put(action, count);
    }

    public void addAbility(Ability ability) {
        this.abilities.add(ability);
    }

    public void addItem(Item item) {
        this.items.add(item);
    }
}
