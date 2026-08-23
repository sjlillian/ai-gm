package aigm.gamestate.campaign;

import aigm.gamestate.Contact;

/** Crew-type contacts from Core Rulebook pp. 98–121. */
public enum CrewContactEnum implements Contact {

    // --- Assassins (p. 99) ---
    ASSASSINS_TREV("Trev", "A gang boss."),
    ASSASSINS_LYDRA("Lydra", "A deal broker."),
    ASSASSINS_IRIMINA("Irimina", "A vicious noble."),
    ASSASSINS_KARLOS("Karlos", "A bounty hunter."),
    ASSASSINS_EXETER("Exeter", "A Spirit Warden."),
    ASSASSINS_SEVOY("Sevoy", "A merchant lord."),

    // --- Bravos (p. 103) ---
    BRAVOS_MEG("Meg", "A pit-fighter."),
    BRAVOS_CONWAY("Conway", "A Bluecoat."),
    BRAVOS_KELLER("Keller", "A blacksmith."),
    BRAVOS_TOMAS("Tomas", "A physicker."),
    BRAVOS_WALKER("Walker", "A ward boss."),
    BRAVOS_LUTES("Lutes", "A tavern owner."),

    // --- Cult (p. 107) ---
    CULT_GAGAN("Gagan", "An academic."),
    CULT_ADIKIN("Adikin", "An occultist."),
    CULT_HUTCHINS("Hutchins", "An antiquarian."),
    CULT_MORIYA("Moriya", "A spirit trafficker."),
    CULT_MATEAS_KLINE("Mateas Kline", "A noble."),
    CULT_BENNETT("Bennett", "An astronomer."),

    // --- Hawkers (p. 111) ---
    HAWKERS_ROLAN_WOTT("Rolan Wott", "A magistrate."),
    HAWKERS_LAROZE("Laroze", "A Bluecoat."),
    HAWKERS_LYDRA("Lydra", "A deal broker."),
    HAWKERS_HOXLEY("Hoxley", "A smuggler."),
    HAWKERS_ANYA("Anya", "A dilettante."),
    HAWKERS_MARLO("Marlo", "A gang boss."),

    // --- Shadows (p. 115) ---
    SHADOWS_DOWLER("Dowler", "An explorer."),
    SHADOWS_LAROZE("Laroze", "A Bluecoat."),
    SHADOWS_AMANCIO("Amancio", "A deal broker."),
    SHADOWS_FITZ("Fitz", "A collector."),
    SHADOWS_ADELAIDE_PHROAIG("Adelaide Phroaig", "A noble."),
    SHADOWS_RIGNEY("Rigney", "A tavern owner."),

    // --- Smugglers (p. 119) ---
    SMUGGLERS_ELYNN("Elynn", "A dock worker."),
    SMUGGLERS_ROLAN("Rolan", "A drug dealer."),
    SMUGGLERS_SERA("Sera", "An arms dealer."),
    SMUGGLERS_NYELLE("Nyelle", "A spirit trafficker."),
    SMUGGLERS_DECKER("Decker", "An anarchist."),
    SMUGGLERS_ESME("Esme", "A tavern owner.");

    private final String name;
    private final String description;

    CrewContactEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Scope getScope() {
        return Scope.CREW;
    }

}
