package aigm.gamestate.player;

public enum ViceKind {

    FAITH("Faith", "You’re dedicated to an unseen power, forgotten god, ancestor, etc."),
    GAMBLING("Gambling", "You crave games of chance, betting on sporting events, etc."),
    LUXURY("Luxury", "Expensive or ostentatious displays of opulence."),
    OBLIGATION("Obligation", "You’re devoted to a family, a cause, an organization, a charity, etc."),
    PLEASURE("Pleasure", "Gratification from lovers, food, drink, drugs, art, theater, etc."),
    STUPOR("Stupor", "You seek oblivion in the abuse of drugs, drink to excess, getting beaten to a pulp in the fighting pits, etc."),
    WEIRD("Weird", "You experiment with strange essences, consort with rogue spirits, observe bizarre rituals or taboos, etc.");

    private final String name;
    private final String description;

    ViceKind(String name, String description) {
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
