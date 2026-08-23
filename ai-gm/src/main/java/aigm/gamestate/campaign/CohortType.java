package aigm.gamestate.campaign;

/** Gang types from Core Rulebook p. 96. Experts use a freeform specialty instead. */
public enum CohortType {

    THUGS("Thugs", "Dangerous and plenty. Skirmish, wreck, intimidate."),
    SKULKS("Skulks", "Stealth and infiltration. Prowl, finesse, survey."),
    ROVERS("Rovers", "Travel and transport. Hunt, swerve, survive the deathlands."),
    ADEPTS("Adepts", "Scholars and occultists. Attune, study, command the strange."),
    ROOKS("Rooks", "Social operators. Consort, sway, deceive.");

    private final String name;
    private final String description;

    CohortType(String name, String description) {
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
