package aigm.gamestate.player;

/** Declared load for a score. Items are marked as they become relevant, not listed in full up front. */
public enum Load {
    LIGHT(3),
    NORMAL(5),
    HEAVY(6);

    private final int itemSlots;

    Load(int itemSlots) {
        this.itemSlots = itemSlots;
    }

    public int getItemSlots() {
        return itemSlots;
    }
}
