package aigm.gamestate;

/**
 * Action-roll effect: how much the action can accomplish. Clock ticks are a common
 * (not exclusive) reading: zero 0, limited 1, standard 2, great 3, extreme 5.
 */
public enum Effect {
    ZERO("You achieve nothing; the action has no meaningful impact.", 0),
    LIMITED("You achieve a partial or weak effect.", 1),
    STANDARD("You achieve what we'd expect as normal with this action.", 2),
    GREAT("You achieve more than usual.", 3),
    EXTREME("You achieve far more than usual.", 5);

    private final String description;
    private final int clockTicks;

    Effect(String description, int clockTicks) {
        this.description = description;
        this.clockTicks = clockTicks;
    }

    public String getDescription() {
        return description;
    }

    public int getClockTicks() {
        return clockTicks;
    }
}
