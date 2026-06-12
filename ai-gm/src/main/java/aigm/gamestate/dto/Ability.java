package aigm.gamestate.dto;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

}
