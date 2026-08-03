package aigm.gamestate.player;

public enum ItemEnum implements Item {
    WEAPON("Weapon", "A weapon used for combat.", true),
    ARMOR("Armor", "Protective gear to reduce damage.", true),
    GADGET("Gadget", "A useful device or tool.", true),
    CONSUMABLE("Consumable", "An item that can be consumed for benefits.", true),
    MISC("Miscellaneous", "Other items that don't fit into the above categories.", true);

    private final String name;
    private final String description;
    private final boolean fine;

    ItemEnum(String name, String description, boolean isFine) {
        this.name = name;
        this.description = description;
        this.fine = isFine;
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
    public boolean isFine() {
        return fine;
    }

}
