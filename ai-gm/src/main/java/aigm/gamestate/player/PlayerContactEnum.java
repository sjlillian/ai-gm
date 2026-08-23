package aigm.gamestate.player;

import aigm.gamestate.Contact;

/** Playbook friends and rivals from Core Rulebook pp. 61–87. */
public enum PlayerContactEnum implements Contact {

    // --- Cutter (p. 61) ---
    CUTTER_MARLANE("Marlane", "A pugilist."),
    CUTTER_CHAEL("Chael", "A vicious thug."),
    CUTTER_MERCY("Mercy", "A cold killer."),
    CUTTER_GRACE("Grace", "An extortionist."),
    CUTTER_SAWTOOTH("Sawtooth", "A physicker."),

    // --- Hound (p. 65) ---
    HOUND_STEINER("Steiner", "An assassin."),
    HOUND_CELENE("Celene", "A sentinel."),
    HOUND_MELVIR("Melvir", "A physicker."),
    HOUND_VELERIS("Veleris", "A spy."),
    HOUND_CASTA("Casta", "A bounty hunter."),

    // --- Leech (p. 69) ---
    LEECH_STAZIA("Stazia", "An apothecary."),
    LEECH_VELDREN("Veldren", "A psychonaut."),
    LEECH_ECKERD("Eckerd", "A corpse thief."),
    LEECH_JUL("Jul", "A blood dealer."),
    LEECH_MALISTA("Malista", "A priestess."),

    // --- Lurk (p. 73) ---
    LURK_TELDA("Telda", "A beggar."),
    LURK_DARMOT("Darmot", "A Bluecoat."),
    LURK_FRAKE("Frake", "A locksmith."),
    LURK_ROSLYN_KELLIS("Roslyn Kellis", "A noble."),
    LURK_PETRA("Petra", "A city clerk."),

    // --- Slide (p. 77) ---
    SLIDE_BRYL("Bryl", "A drug dealer."),
    SLIDE_BAZSO_BAZ("Bazso Baz", "A gang leader."),
    SLIDE_KLYRA("Klyra", "A tavern owner."),
    SLIDE_NYRIX("Nyrix", "A prostitute."),
    SLIDE_HARKER("Harker", "A jail bird."),

    // --- Spider (p. 81) ---
    SPIDER_SALIA("Salia", "An information broker."),
    SPIDER_AUGUS("Augus", "A master architect."),
    SPIDER_JENNAH("Jennah", "A servant."),
    SPIDER_RIVEN("Riven", "A chemist."),
    SPIDER_JEREN("Jeren", "A Bluecoat archivist."),

    // --- Whisper (p. 85) ---
    WHISPER_NYRYX("Nyryx", "A possessor ghost."),
    WHISPER_SCURLOCK("Scurlock", "A vampire."),
    WHISPER_SETARRA("Setarra", "A demon."),
    WHISPER_QUELLYN("Quellyn", "A witch."),
    WHISPER_FLINT("Flint", "A spirit trafficker.");

    private final String name;
    private final String description;

    PlayerContactEnum(String name, String description) {
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
        return Scope.PLAYER;
    }

}
