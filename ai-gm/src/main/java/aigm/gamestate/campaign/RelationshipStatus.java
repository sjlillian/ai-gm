package aigm.gamestate.campaign;

/**
 * Faction status is a scale from +3 to −3. War is −3 and can be true of several factions at once.
 * Rivals (hunting-grounds crews, playbook rivals) are a separate relationship, not a status rank.
 */
public enum RelationshipStatus {
    ALLIES(3),
    FRIENDLY(2),
    HELPFUL(1),
    NEUTRAL(0),
    INTERFERING(-1),
    HOSTILE(-2),
    WAR(-3);

    private final int rank;

    RelationshipStatus(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public boolean isAtWar() {
        return this == WAR;
    }
}
