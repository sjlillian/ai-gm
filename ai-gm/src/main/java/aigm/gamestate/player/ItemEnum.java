package aigm.gamestate.player;

/** Standard items from Blades in the Dark Core Rulebook p. 88. Playbook-specific gear stays on the playbook. */
public enum ItemEnum implements Item {
    A_BLADE_OR_TWO(
        "A Blade or Two",
        "A fighting knife, paired swords, rapier and stiletto, heavy cleaver, or similar. [1 load]",
        1,
        false),
    THROWING_KNIVES(
        "Throwing Knives",
        "Six small, light blades. [1 load]",
        1,
        false),
    A_PISTOL(
        "A Pistol",
        "A heavy, single-shot, breech-loading firearm. Devastating at 20 paces, slow to reload. [1 load]",
        1,
        false),
    A_LARGE_WEAPON(
        "A Large Weapon",
        "A two-handed weapon: battle-axe, greatsword, warhammer, pole-arm, hunting rifle, blunderbuss, bow, or crossbow. [2 load]",
        2,
        false),
    AN_UNUSUAL_WEAPON(
        "An Unusual Weapon",
        "A curiosity or tool turned into a weapon: whip, flail, hatchet, shovel, chain, razor-edged fan, steel-toed boots. [1 load]",
        1,
        false),
    ARMOR(
        "Armor",
        "A thick leather tunic with a high collar, reinforced gloves and boots. [2 load]",
        2,
        false),
    HEAVY(
        "+Heavy",
        "Chain mail, metal plates, a metal helm. Load is in addition to normal armor — 5 load total. [3 load]",
        3,
        false),
    BURGLARY_GEAR(
        "Burglary Gear",
        "Lockpicks, a small pry-bar, oil to silence hinges, wire and fishing hooks, a pouch of fine sand. [1 load]",
        1,
        false),
    CLIMBING_GEAR(
        "Climbing Gear",
        "Ropes, grappling hooks, chalk, a climbing harness, iron pitons, and a small mallet. [2 load]",
        2,
        false),
    DOCUMENTS(
        "Documents",
        "Slim volumes including a registry of nobility, City Watch commanders, and notable citizens; blank pages, ink, pen, and maps. [1 load]",
        1,
        false),
    ARCANE_IMPLEMENTS(
        "Arcane Implements",
        "Quicksilver, black salt, a small-stone spirit anchor, a spirit bottle, and a vial of electroplasm made to break on impact. [1 load]",
        1,
        false),
    SUBTERFUGE_SUPPLIES(
        "Subterfuge Supplies",
        "Theatrical make-up, blank documents for forgery, costume jewelry, a reversible cloak and distinctive hat, a forged badge of office. [1 load]",
        1,
        false),
    DEMOLITION_TOOLS(
        "Demolition Tools",
        "A sledgehammer and iron spikes, a heavy drill, and a crowbar. [2 load]",
        2,
        false),
    TINKERING_TOOLS(
        "Tinkering Tools",
        "Jeweler's loupe, tweezers, a small hammer, pliers, screwdriver, and other detailed mechanist tools. [1 load]",
        1,
        false),
    LANTERN(
        "Lantern",
        "A simple oil lantern, a fancy electroplasmic lamp, or other light source. [1 load]",
        1,
        false),
    SPIRITBANE_CHARM(
        "Spiritbane Charm",
        "A small arcane trinket that ghosts prefer to avoid. [0 load]",
        0,
        false),

    // --- Cutter (p. 63) ---
    FINE_HAND_WEAPON(
        "Fine Hand Weapon",
        "A finely crafted one-handed melee weapon of your choice. [1 load]",
        1,
        true),
    FINE_HEAVY_WEAPON(
        "Fine Heavy Weapon",
        "A finely crafted two-handed melee weapon of your choice. More reach and hits harder than a standard weapon. [2 load]",
        2,
        true),
    SCARY_WEAPON_OR_TOOL(
        "Scary Weapon or Tool",
        "A scary-looking hand weapon or tool. Increased effect when you intimidate, not increased harm in combat. [1 load]",
        1,
        false),
    MANACLES_AND_CHAIN(
        "Manacles & Chain",
        "A set of heavy manacles and chain, suitable for restraining a prisoner. [0 load]",
        0,
        false),
    RAGE_ESSENCE_VIAL(
        "Rage Essence Vial",
        "A single dose that greatly enhances strength, resistance to pain, and irrational aggression for several minutes. You suffer two consequences: \"Can't Tell Friend From Foe\" and \"Can't Stop Until They're All Broken.\" [0 load]",
        0,
        false),

    // --- Hound (p. 67) ---
    FINE_PAIR_OF_PISTOLS(
        "Fine Pair of Pistols",
        "A matched pair of handguns, made for greater accuracy, with double barrels that allow two shots before reloading. [1 load]",
        1,
        true),
    FINE_LONG_RIFLE(
        "Fine Long Rifle",
        "A finely crafted hunting rifle, deadly at long range, unwieldy in close quarters. [2 load]",
        2,
        true),
    ELECTROPLASMIC_AMMUNITION(
        "Electroplasmic Ammunition",
        "A bandolier of electroplasmic ammo, especially potent against spirits, but less effective against physical targets. [1 load]",
        1,
        false),
    TRAINED_HUNTING_PET(
        "Trained Hunting Pet",
        "Your animal companion obeys your commands and anticipates your actions. Cohort (Expert: Hunter). [0 load]",
        0,
        false),
    SPYGLASS(
        "Spyglass",
        "A brass tube with lenses that allow long-distance vision. Collapsible. May attach to a rifle. [1 load]",
        1,
        false),

    // --- Leech (p. 71) ---
    FINE_TINKERING_TOOLS(
        "Fine Tinkering Tools",
        "A finely crafted set of tools for detailed mechanist work. A jeweler's loupe. Measuring devices. [1 load]",
        1,
        true),
    FINE_WRECKER_TOOLS(
        "Fine Wrecker Tools",
        "A specialized set of tools for sabotage and destruction. Drill, mallet and spikes, prybar, electroplasmic battery, acid, spark-torch cutter. [2 load]",
        2,
        true),
    BLOWGUN_AND_DARTS(
        "Blowgun & Darts, Syringes",
        "A small tube and darts that can be filled from alchemy flasks. Empty syringes. [0 load]",
        0,
        false),
    BANDOLIER_OF_ALCHEMICALS(
        "Bandolier of Alchemicals",
        "A strap fitted with padded pouches to hold three flasks of alchemical agents. [1 load]",
        1,
        false),
    GADGETS(
        "Gadgets",
        "You may create gadgets during downtime by Tinkering with tools and materials. Track the load for each gadget you deploy. [1+ load]",
        1,
        false),

    // --- Lurk (p. 75) ---
    FINE_LOCKPICKS(
        "Fine Lockpicks",
        "A finely crafted set of tools to disable and circumvent locks. [0 load]",
        0,
        true),
    FINE_SHADOW_CLOAK(
        "Fine Shadow Cloak",
        "A hooded cloak of rare Iruvian shadow-silk that blends into the darkness. Improves your effect level when you sneak around. [1 load]",
        1,
        true),
    LIGHT_CLIMBING_GEAR(
        "Light Climbing Gear",
        "A well-crafted set of climbing gear that is less bulky than a standard set. [1 load]",
        1,
        false),
    SILENCE_POTION_VIAL(
        "Silence Potion Vial",
        "A vial of golden liquid that negates all sound within 10 paces of the drinker for several moments. [0 load]",
        0,
        false),
    DARK_SIGHT_GOGGLES(
        "Dark-Sight Goggles",
        "An arcane device that allows the wearer to see in pitch darkness as if it were well-lit. [1 load]",
        1,
        false),

    // --- Slide (p. 79) ---
    FINE_CLOTHES_AND_JEWELRY(
        "Fine Clothes & Jewelry",
        "An outfit of such fine make as to pass you off as a wealthy noble. [0 load] If carried as a second outfit to change into, it counts as 2 load.",
        0,
        true),
    FINE_DISGUISE_KIT(
        "Fine Disguise Kit",
        "A theatrical make-up kit with expert appliances to fool the eye. May increase the effect of your deceptive actions. [1 load]",
        1,
        true),
    FINE_LOADED_DICE(
        "Fine Loaded Dice, Trick Cards",
        "Gambling accouterments subtly altered to favor particular outcomes. May increase the effect of your deceptive actions. [0 load]",
        0,
        true),
    TRANCE_POWDER(
        "Trance Powder",
        "A dose of popular drug that induces a calm, suggestible mental state similar to hypnotism. [0 load]",
        0,
        false),
    CANE_SWORD(
        "A Cane-Sword",
        "A slim sword and its sheath, disguised as a noble's cane. The disguise will fool a cursory inspection. [1 load]",
        1,
        false),

    // --- Spider (p. 83) ---
    FINE_COVER_IDENTITY(
        "Fine Cover Identity",
        "Paperwork, planted stories and rumors, and false relationships sufficient to pass as a different person. [0 load]",
        0,
        true),
    FINE_BOTTLE_OF_WHISKEY(
        "Fine Bottle of Whiskey",
        "A rare distillation from your personal collection, potent both in its alcohol and its ability to impress. [1 load]",
        1,
        true),
    BLUEPRINTS(
        "Blueprints",
        "A folio of useful architectural drawings and city plans. [1 load]",
        1,
        false),
    VIAL_OF_SLUMBER_ESSENCE(
        "Vial of Slumber Essence",
        "A dose sufficient to put someone to sleep for an hour. The sleep isn't supernatural, but it is deep. [0 load]",
        0,
        false),
    CONCEALED_PALM_PISTOL(
        "Concealed Palm Pistol",
        "A small firearm with a weak charge, easily concealed in a sleeve or waistcoat. Extremely limited range; very difficult to detect even if you're searched. [0 load]",
        0,
        false),

    // --- Whisper (p. 87) ---
    FINE_LIGHTNING_HOOK(
        "Fine Lightning Hook",
        "A long two-handed pole with a loop of heavy wire connected to an electroplasmic capacitor. Collapses into a compact form. Suitable for grappling a spirit into a spirit bottle. [1 load]",
        1,
        true),
    FINE_SPIRIT_MASK(
        "Fine Spirit Mask",
        "An arcane item that allows the trained user to see supernatural energies in great detail. Also affords some protection against ghostly possession. [1 load]",
        1,
        true),
    SPIRIT_BOTTLES(
        "Spirit Bottles (2)",
        "Arcane cylinders of metal and crystal, the size of a loaf of bread, used to trap a spirit. [1 load]",
        1,
        false),
    GHOST_KEY(
        "Ghost Key",
        "An arcane device that can open ghost doors into the echo of the city trapped in the ghost field. [0 load]",
        0,
        false),
    DEMONBANE_CHARM(
        "Demonbane Charm",
        "An arcane trinket that demons prefer to avoid. [0 load]",
        0,
        false);

    private final String name;
    private final String description;
    private final int load;
    private final boolean fine;

    ItemEnum(String name, String description, int load, boolean isFine) {
        this.name = name;
        this.description = description;
        this.load = load;
        this.fine = isFine;
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
    public int getLoad() {
        return load;
    }

    @Override
    public boolean isFine() {
        return fine;
    }

}
