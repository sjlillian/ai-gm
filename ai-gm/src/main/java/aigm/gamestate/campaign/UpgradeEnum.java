package aigm.gamestate.campaign;

public enum UpgradeEnum implements Upgrade {

    HIDEOUT("Hideout", "A secret base of operations for your crew.", 1000),
    VEHICLE("Vehicle", "A specialized vehicle for your crew's operations.", 500),
    WEAPONRY("Weaponry", "Advanced weapons and equipment for your crew.", 750);

    private final String name;
    private final String description;
    private final int cost;

    UpgradeEnum(String name, String description, int cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
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
    public int getCost() {
        return cost;
    }

}
