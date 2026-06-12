package aigm.gamestate.dto;

public class Ability {

    private enum Scope {
        PLAYER, CREW;
    }

    private String name;
    private String description;
    private Scope scope;

}
