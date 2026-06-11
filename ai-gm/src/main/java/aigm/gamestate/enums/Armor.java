package aigm.gamestate.enums;

public enum Armor {
    STANDARD("2 Load"),
    HEAVY("3 Load"),
    SPECIAL("Ability");

    private final String cost;

    Armor(String cost) {
        this.cost = cost;
    }

    public String getCost() {
        return cost;
    }
}
