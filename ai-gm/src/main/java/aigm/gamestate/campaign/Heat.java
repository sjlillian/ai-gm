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

    public static final int HEAT_BOXES = 9;

    private static final Clock DEFAULT_HEAT_CLOCK = new Clock("Heat", 0, HEAT_BOXES);

    public Heat() {
        this(DEFAULT_HEAT_CLOCK, WantedLevel.ZERO);
    }

    public Heat updateHeat(int delta) {
        if (wantedLevel == WantedLevel.FOUR) {
            return new Heat(heat.tick(delta), wantedLevel);
        }

        Clock.Overflow overflow = heat.tickOverflowing(delta);
        WantedLevel wanted = wantedLevel;
        Clock remaining = overflow.clock();

        for (int i = 0; i < overflow.completions(); i++) {
            if (wanted == WantedLevel.FOUR) {
                return new Heat(new Clock(heat.name(), HEAT_BOXES, HEAT_BOXES), wanted);
            }
            wanted = wanted.increase();
        }

        return new Heat(remaining, wanted);
    }
}
