package aigm.gamestate.campaign;

/** Doskvol districts from the Core Rulebook city guide. */
public enum District {

    BARROWCLEFT(
        "Barrowcleft",
        "Farms, grocers, and the radiant energy of the leviathan-blood lamps that keep crops alive. Quiet, clannish, and a place to hide among honest work."),
    BRIGHTSTONE(
        "Brightstone",
        "The richest civilian district: mansions, ministries, and private security. Scores here mean heat, leverage, and very fine loot."),
    CHARHOLLOW(
        "Charhollow",
        "Crowded laborers' tenements, cookfires, and tight-knit blocks. Cheap rooms, cheap vice, and plenty of people who owe someone."),
    CHARTERHALL(
        "Charterhall",
        "Law, academia, and civic machinery — courts, the college, newspapers, and offices that keep records worth stealing."),
    COALRIDGE(
        "Coalridge",
        "Factories, foundries, and company housing under a pall of smoke. Union trouble, industrial sabotage, and grimy coin."),
    CROWS_FOOT(
        "Crow's Foot",
        "Crowded streets fought over by the Lampblacks and the Crows. The classic scoundrel neighborhood: turf, taverns, and knives."),
    THE_DOCKS(
        "The Docks",
        "Cargo, sailors, Rail Jacks, and things that crawl off ships after dark. Smuggling is a way of life."),
    DUNSLOUGH(
        "Dunslough",
        "A wretched dump of laborers, prisoners, and the lost. Ironhook's shadow. Desperate people, cheap muscle, ugly secrets."),
    NIGHTMARKET(
        "Nightmarket",
        "Bazaars that never really close, contraband, and the whisper of the lost district beyond. Buy anything; sell anything."),
    SILKSHORE(
        "Silkshore",
        "Canals, pleasure houses, artists, and vice dens. Pretty on the surface, rotten in the water."),
    SIX_TOWERS(
        "Six Towers",
        "Crumbling manors of families that used to matter. Ghosts, faded crests, and people clinging to old names."),
    WHITECROWN(
        "Whitecrown",
        "Imperial palaces, Leviathan Hunter villas, and the seats of true power. Almost nobody belongs here — which is why a crew might try.");

    private final String name;
    private final String description;

    District(String name, String description) {
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
