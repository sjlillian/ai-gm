package aigm.gamestate.campaign;

/** Cohort flaws from Core Rulebook p. 96. */
public enum CohortFlaw {

    PRINCIPLED("Principled", "The cohort has an ethic or value they won't betray."),
    SAVAGE("Savage", "The cohort is excessively violent and cruel."),
    UNRELIABLE("Unreliable", "The cohort isn't always available, due to other obligations, stupefaction, etc."),
    WILD("Wild", "The cohort is drunken, debauched, and loud-mouthed.");

    private final String name;
    private final String description;

    CohortFlaw(String name, String description) {
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
