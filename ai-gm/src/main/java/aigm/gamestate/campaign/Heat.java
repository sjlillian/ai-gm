package aigm.gamestate.campaign;

import aigm.gamestate.Clock;

public record Heat(Clock heat, WantedLevel wantedLevel) {

    public enum WantedLevel {
        ZERO, ONE, TWO, THREE, FOUR;

        public WantedLevel increase() {
            int next = Math.min(this.ordinal() + 1, values().length - 1);
            return values()[next];
        }

        public WantedLevel decrease() {
            int next = Math.max(this.ordinal() - 1, 0);
            return values()[next];
        }
    }

    private final static Clock DEFAULT_HEAT_CLOCK = new Clock("Heat", 0, 9);

    public Heat() {
        this(DEFAULT_HEAT_CLOCK, WantedLevel.ZERO);
    }

    public Heat updateHeat(int delta) {
        Clock newHeat = heat.tick(delta);
        WantedLevel newWantedLevel = wantedLevel;
        if (newHeat.isComplete())
            return new Heat(DEFAULT_HEAT_CLOCK, wantedLevel.increase());
        return new Heat(newHeat, newWantedLevel);
    }

    public Heat clearOnIncarceration() {
        return new Heat(DEFAULT_HEAT_CLOCK, wantedLevel.decrease());
    }
}
