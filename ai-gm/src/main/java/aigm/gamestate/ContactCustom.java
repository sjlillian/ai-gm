package aigm.gamestate;

public record ContactCustom(String name, String description, Scope scope) implements Contact {

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
