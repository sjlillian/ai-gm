package aigm.gamestate.campaign;

/** Cohort edges from Core Rulebook p. 96. */
public enum CohortEdge {

    FEARSOME("Fearsome", "The cohort is terrifying in aspect and reputation."),
    INDEPENDENT("Independent", "The cohort can be trusted to make good decisions and act on their own in the absence of orders."),
    LOYAL("Loyal", "The cohort can't be bribed or turned against you."),
    TENACIOUS("Tenacious", "The cohort won't be deterred from a course of action.");

    private final String name;
    private final String description;

    CohortEdge(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
