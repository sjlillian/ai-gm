package aigm.gamestate;

public record AbilityCustom(String name, String description, Scope scope) implements Ability {

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

}
