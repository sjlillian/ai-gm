package aigm.gamestate.player;

public enum Action {
    HUNT(Attribute.INSIGHT),
    STUDY(Attribute.INSIGHT),
    SURVEY(Attribute.INSIGHT),
    TINKER(Attribute.INSIGHT),
    FINESSE(Attribute.PROWESS),
    PROWL(Attribute.PROWESS),
    SKIRMISH(Attribute.PROWESS),
    WRECK(Attribute.PROWESS),
    ATTUNE(Attribute.RESOLVE),
    COMMAND(Attribute.RESOLVE),
    CONSORT(Attribute.RESOLVE),
    SWAY(Attribute.RESOLVE);

    private final Attribute attribute;

    Action(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public String getDescription() {
        return switch (this) {
            case HUNT -> "Carefully track a target.";
            case STUDY -> "Scrutinize details and interpret evidence.";
            case SURVEY -> "Observe a location or situation to anticipate what's coming.";
            case TINKER -> "Fiddle with devices and mechanisms.";
            case FINESSE -> "Employ dextrous manipulation or subtle misdirection.";
            case PROWL -> "Traverse skillfully and quietly.";
            case SKIRMISH -> "Brawl and fight up close.";
            case WRECK -> "Unleash savage force.";
            case ATTUNE -> "Open your mind to the ghost field or rituals.";
            case COMMAND -> "Compel swift obedience.";
            case CONSORT -> "Socialize with friends and contacts.";
            case SWAY -> "Influence with charm, logic, or deception.";
        };
    }
}
