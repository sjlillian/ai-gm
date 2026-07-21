package aigm.gamestate.dto;

public class Ability {

    private enum Ascope {
        PLAYER, CREW;
    }

    private String name;
    private String description;
    private Ascope scope;

}
