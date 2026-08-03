package aigm.gamestate;

public enum AbilityEnum implements Ability{

    ABILITY_1("Ability 1", "Description 1", Scope.PLAYER),
    ABILITY_2("Ability 2", "Description 2", Scope.CREW);

    private final String name;
    private final String description;
    private final Scope scope;

    AbilityEnum(String name, String description, Scope scope) {
        this.name = name;
        this.description = description;
        this.scope = scope;
    }

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
