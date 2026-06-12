package aigm.gamestate.dto;

import java.util.List;

import aigm.gamestate.enums.Action;

public class Playbook {

    private String name;
    private String xpTrigger;
    private List<Action> startingActions;
    private List<Ability> abilities;
    private List<Item> items;

    public Playbook() {
        // default constructor for serialization
    }

    public Playbook(String name, String xpTrigger, List<Action> startingActions, List<Ability> abilities, List<Item> items) {
        this.name = name;
        this.xpTrigger = xpTrigger;
        this.startingActions = startingActions;
        this.abilities = abilities;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXpTrigger() {
        return xpTrigger;
    }

    public void setXpTrigger(String xpTrigger) {
        this.xpTrigger = xpTrigger;
    }

    public List<Action> getStartingActions() {
        return startingActions;
    }

    public void setStartingActions(List<Action> startingActions) {
        this.startingActions = startingActions;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addAbility(Ability ability) {
        this.abilities.add(ability);
    }

    public void addItem(Item item) {
        this.items.add(item);
    }
}
