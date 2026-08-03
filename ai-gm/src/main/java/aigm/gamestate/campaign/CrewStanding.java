package aigm.gamestate.campaign;

import aigm.gamestate.Clock;

public record CrewStanding(
    Reputation reputation,
    Tier tier,
    Hold hold,
    Clock rep,
    int turf
) {

    private enum Reputation {
        AMBITIOUS, BRUTAL, DARING, HONORABLE, PROFESSIONAL, SAVVY, SUBTLE, STRANGE;
    }

    private enum Tier {
        ZERO, ONE, TWO, THREE, FOUR, FIVE;

        public int advancementCost() {
            return this.ordinal() * 8;
        }

        public Tier decrease() {
            int next = Math.max(this.ordinal() - 1, 0);
            return values()[next];
        }

        public Tier increase() {
            int next = Math.min(this.ordinal() + 1, values().length - 1);
            return values()[next];
        }
    }

    private enum Hold {
        STRONG, WEAK;
    }

    public CrewStanding() {
        this(Reputation.AMBITIOUS, Tier.ZERO, Hold.STRONG, new Clock("Rep", 12), 0);
    }

    public int tierAdvancementCost() {
        return tier.increase().advancementCost();
    }

    public CrewStanding addTurf(int amount) {
        return new CrewStanding(reputation, tier, hold, rep, turf + amount);
    }

    public CrewStanding addRep(int amount) {
        if (rep.isComplete()) {
            return new CrewStanding(reputation, tier, Hold.STRONG, new Clock("Rep", 12), turf);
        }
        return new CrewStanding(reputation, tier, hold, rep.tick(amount), turf);
    }

    public CrewStanding advanceHold() {
        if (hold == Hold.WEAK) {
            return new CrewStanding(reputation, tier, Hold.STRONG, rep, turf);
        }
        return this;
    }

    public CrewStanding advanceTier() {
        if (hold == Hold.STRONG) {
            return new CrewStanding(reputation, tier.increase(), Hold.WEAK, rep, turf);
        }
        return this;
    }

    public CrewStanding reduceHold() {
        if (hold == Hold.WEAK) {
            return new CrewStanding(reputation, tier.decrease(), Hold.STRONG, rep, turf);
        }
        return new CrewStanding(reputation, tier, Hold.WEAK, rep, turf);
    }

    public CrewStanding reduceTier() {
        return new CrewStanding(reputation, tier.decrease(), hold, rep, turf);
    }
}