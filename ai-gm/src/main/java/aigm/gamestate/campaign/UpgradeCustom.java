package aigm.gamestate.campaign;

public record UpgradeCustom(String name, String description, int cost) implements Upgrade {

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getCost() {
        return cost;
    }

}
