package aigm.gamestate;

import java.util.List;

import aigm.gamestate.dto.Ability;
import aigm.gamestate.enums.Action;
import aigm.gamestate.dto.Item;

public class Playbook {

    private String name;
    private String xpTrigger;
    private List<Action> startingActions;
    private List<Ability> abilities;
    private List<Item> items;

}
