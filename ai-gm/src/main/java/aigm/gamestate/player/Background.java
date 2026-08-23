package aigm.gamestate.player;

public enum Background {

    ACADEMIC("Academic", "A scholar, a professor or student from Doskvol Academy, a philosopher or journalist, etc."),
    LABORER("Laborer", "A servant, a factory worker, a coach driver, a docker, a sailor, etc."),
    LAW("Law", "An advocate or barrister, a Bluecoat or inspector (or even Spirit Warden), a prison guard from Ironhook, a Rail Jack, etc."),
    TRADE("Trade", "A shopkeeper, a merchant, a skilled crafts-person, a shipping agent, etc."),
    MILITARY("Military", "An Imperial or Skovlander soldier, a mercenary, an intelligence operative, a strategist, a training instructor, etc."),
    NOBLE("Noble", "A dilettante, a courtier, the scion of a fallen house, etc."),
    UNDERWORLD("Underworld", "A street urchin, gang member, young thug, or other outcast who grew up on the streets.");

    private final String name;
    private final String description;

    Background(String name, String description) {
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
