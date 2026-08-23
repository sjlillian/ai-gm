package aigm.gamestate.player;

import java.util.List;

/**
 * Per-score load. Armor is marked to reduce a harm consequence by one level;
 * heavy armor allows a second mark. Special armor is ability-specific.
 */
public record Loadout(
    Load load,
    List<Item> items,
    int armorMarksUsed,
    boolean hasArmor,
    boolean hasHeavyArmor
) {

    public Loadout {
        items = List.copyOf(items);
    }

    public Loadout() {
        this(Load.NORMAL, List.of(), 0, false, false);
    }

    public int armorMarksAvailable() {
        int marks = 0;
        if (hasArmor) {
            marks += 1;
        }
        if (hasHeavyArmor) {
            marks += 1;
        }
        return marks;
    }

    public boolean canMarkArmor() {
        return armorMarksUsed < armorMarksAvailable();
    }

    public Loadout markArmor() {
        if (!canMarkArmor()) {
            return this;
        }
        return new Loadout(load, items, armorMarksUsed + 1, hasArmor, hasHeavyArmor);
    }

    public Loadout resetForNewScore() {
        return new Loadout(load, items, 0, hasArmor, hasHeavyArmor);
    }
}
