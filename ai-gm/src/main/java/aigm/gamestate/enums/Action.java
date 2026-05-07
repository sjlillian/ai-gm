package aigm.gamestate.enums;

public enum Action {
    HUNT(Attribute.INSIGHT),
    STUDY(Attribute.INSIGHT),
    SURVEY(Attribute.INSIGHT),
    TINKER(Attribute.PROWESS),
    FINESSE(Attribute.PROWESS),
    PROWL(Attribute.PROWESS),
    SKRIMISH(Attribute.PROWESS),
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
}


