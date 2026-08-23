package aigm.gamestate.player;

public enum Heritage {

    AKOROS("Akoros", "Akoros is the largest and most industrialized land in the Imperium, " + 
    "and is home to the capitol city as well as Duskwall itself. They're known as a diverse conglomeration of cultures that have grown together in close proximity for centuries, " +
    "somewhat like Europe."),
    DAGGER_ISLES("Dagger Isles", "If you want to be a rootless wanderer you could be from the Dagger Isles. " +
    "People there often grow up on ships and travel a lot before settling down. " +
    "They're known as corsairs and merchants who live without lightning barriers— " +
    "dealing with spirits in other ways."),
    IRUVIA("Iruvia", "If you want to be from a culture considered “foreign” by the locals, you could " +
    "be from Iruvia, a rich and powerful desert kingdom far to the south. It's " +
    "another diverse land of varying cultures similar to old Persia, Egypt, and India."),
    SEVEROS("Severos", "If you want to be from a place considered “wild” by the rest of the empire, you " +
    "could be from Severos. Outside the few imperial settlements, most Severosi " +
    "live in nomadic horse-tribes scattered across the blasted deathlands, surviving " +
    "within the ruins of ancient arcane fortresses which still repel spirits."),
    SKOVLAN("Skovlan", "If you want to be from a marginalized people, you could be from Skovlan, " +
    "the island kingdom just across the sea from Doskvol. Skovlan was last to be " +
    "brought under imperial rule, over the course of the 36-year Unity War (which " +
    "ended only a few years ago). Many Skovlander refugees who lost their homes " +
    "and jobs in the destruction of the war have come to Doskvol seeking new " +
    "opportunities."),
    TYCHEROS("Tycheros", "If you want to be weird, you can be from Tycheros. It's a semi-mythical place, " +
    "far away beyond the northern void sea. Everyone says that the people there are " +
    "part-demon. If you choose Tycherosi heritage, also create a demonic telltale " +
    "(like black shark eyes, feathers instead of hair, etc.) that marks your character.");

    private final String name;
    private final String description;

    Heritage(String name, String description) {
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
