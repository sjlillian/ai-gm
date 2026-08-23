package aigm.gamestate.player;

public record ItemCustom(String name, String description, int load, boolean fine) implements Item {

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getLoad() {
        return load;
    }

    @Override
    public boolean isFine() {
        return fine;
    }

}
