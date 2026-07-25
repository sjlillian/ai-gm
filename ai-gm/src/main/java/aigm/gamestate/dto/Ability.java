package aigm.gamestate.dto;

import lombok.Data;

@Data
public class Ability {

    private enum Scope {
        PLAYER, CREW;
    }

    private String name;
    private String description;
    private Scope scope;

    public Ability() {
        // default constructor for serialization
    }

    public Ability(String name, String description, Scope scope) {
        this.name = name;
        this.description = description;
        this.scope = scope;
    }
}
