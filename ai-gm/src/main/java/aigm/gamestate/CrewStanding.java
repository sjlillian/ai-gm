package aigm.gamestate;

public class CrewStanding {

    private enum Reputation {
        AMBITIOUS, BRUTAL, DARING, HONORABLE, PROFESSIONAL, SAVVY, SUBTLE, STRANGE;
    }

    private enum Tier {
        ZERO, ONE, TWO, THREE, FOUR, FIVE;
    }

    private enum Hold {
        STRONG, WEAK;
    }

    private Reputation reputation;
    private Tier tier;
    private Hold hold;
    private Clock rep;
    private int turf;

    public CrewStanding() {
        this.reputation = Reputation.AMBITIOUS; // Default starting reputation
        this.tier = Tier.ZERO; // Default starting tier
        this.hold = Hold.STRONG; // Default starting hold
        this.rep = new Clock("reputation", 12); // Reputation clock with 12 segments
        this.turf = 0;
    }

    public CrewStanding(Reputation reputation, Tier tier, Hold hold) {
        this.reputation = reputation;
        this.tier = tier;
        this.hold = hold;
        this.rep = new Clock("reputation", 12);
        this.turf = 0;
    }

    public void addTurf(int amount) {
        while (amount > 0) {
            addTurf();
            amount--;
        }
    }

    public void addTurf() {
        if (turf < 6)
            turf++;
    }

    public void addRep(int amount) {
        rep.tick(amount);
    }

    public void advanceHold() {
        if (hold == Hold.WEAK)
            hold = Hold.STRONG;
    }

    public void advanceTier() {
        if (hold == Hold.STRONG) {
            switch (tier) {
                case ZERO: tier = Tier.ONE; break;
                case ONE: tier = Tier.TWO; break;
                case TWO: tier = Tier.THREE; break;
                case THREE: tier = Tier.FOUR; break;
                case FOUR: tier = Tier.FIVE; break;
                default: break; // Already at max tier
            }
        }
    }

    public void reduceHold() {
        if (hold == Hold.STRONG) {
            hold = Hold.WEAK;
        } else if (hold == Hold.WEAK) {
            hold = Hold.STRONG;
            reduceTier();
        }
    }

    public void reduceTier() {
        switch (tier) {
            case FIVE: tier = Tier.FOUR; break;
            case FOUR: tier = Tier.THREE; break;
            case THREE: tier = Tier.TWO; break;
            case TWO: tier = Tier.ONE; break;
            case ONE: tier = Tier.ZERO; break;
            default: break; // Already at lowest tier
        }
    }

    public Reputation getReputation() {
        return reputation;
    }

    public Tier getTier() {
        return tier;
    }

    public Hold getHold() {
        return hold;
    }

    public Clock getRep() {
        return rep;
    }

    public int getTurf() {
        return turf;
    }

}
