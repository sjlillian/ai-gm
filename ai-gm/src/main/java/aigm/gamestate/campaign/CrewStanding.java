package aigm.gamestate.campaign;

import aigm.gamestate.Clock;

/**
 * Crew starts at Tier 0 with weak hold. Rep track is 12 boxes minus turf (minimum 1).
 * Filling rep with weak hold becomes strong hold (free). Filling rep with strong hold
 * lets the crew spend coin equal to new tier × 8 to advance; hold then becomes weak.
 */
public record CrewStanding(
    Reputation reputation,
    Tier tier,
    Hold hold,
    Clock rep,
    int turf
) {

    public static final int BASE_REP_TRACK = 12;

    public enum Reputation {
        AMBITIOUS, BRUTAL, DARING, HONORABLE, PROFESSIONAL, SAVVY, SUBTLE, STRANGE
    }

    public enum Tier {
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

    public enum Hold {
        STRONG, WEAK
    }

    public CrewStanding() {
        this(Reputation.AMBITIOUS, Tier.ZERO, Hold.WEAK, new Clock("Rep", BASE_REP_TRACK), 0);
    }

    public int repTrackSize() {
        return Math.max(1, BASE_REP_TRACK - Math.max(0, turf));
    }

    public int tierAdvancementCost() {
        return tier.increase().advancementCost();
    }

    public boolean canAdvanceHold() {
        return rep.isComplete() && hold == Hold.WEAK;
    }

    public boolean canAdvanceTier() {
        return rep.isComplete() && hold == Hold.STRONG && tier != Tier.FIVE;
    }

    public CrewStanding addTurf(int amount) {
        int newTurf = Math.max(0, turf + amount);
        int newMax = Math.max(1, BASE_REP_TRACK - newTurf);
        Clock resized = rep.withMax(newMax);
        return new CrewStanding(reputation, tier, hold, resized, newTurf);
    }

    public CrewStanding addRep(int amount) {
        if (rep.isComplete()) {
            return this;
        }
        return new CrewStanding(reputation, tier, hold, rep.tick(amount), turf);
    }

    /** Weak hold → strong hold, then clear the rep track. Coin is not spent. */
    public CrewStanding advanceHold() {
        if (!canAdvanceHold()) {
            return this;
        }
        return new CrewStanding(reputation, tier, Hold.STRONG, resetRep(), turf);
    }

    /** Strong hold → next tier and weak hold, then clear the rep track. Caller must spend coin first. */
    public CrewStanding advanceTier() {
        if (!canAdvanceTier()) {
            return this;
        }
        return new CrewStanding(reputation, tier.increase(), Hold.WEAK, resetRep(), turf);
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

    public CrewStanding withReputation(Reputation reputation) {
        return new CrewStanding(reputation, tier, hold, rep, turf);
    }

    private Clock resetRep() {
        return new Clock("Rep", 0, repTrackSize());
    }
}
