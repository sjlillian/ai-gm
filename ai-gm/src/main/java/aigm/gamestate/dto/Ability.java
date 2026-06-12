package aigm.gamestate.dto;

public class Ability {

    private enum scope {
        PLAYER, CREW;
    }

    private String name;
    private String description;
    private Scope scope;

}
