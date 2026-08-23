package aigm.gamestate.campaign;

/**
 * Common crew upgrades from Blades in the Dark Core Rulebook p. 95.
 * {@link #getCost()} is upgrade boxes (crew XP advances), not coin.
 * Crew-type upgrades (Rigging, Elite Thugs, Hardened, etc.) belong on the crew type.
 */
public enum UpgradeEnum implements Upgrade {

    CARRIAGE_HOUSE(
        "Carriage House",
        "You have a carriage, two goats to pull it, and a stable.",
        1),
    CARRIAGE_HOUSE_II(
        "Carriage House II",
        "The carriage is armored, with larger, swifter goats.",
        1),
    BOAT_HOUSE(
        "Boat House",
        "You have a boat, a dock on a waterway, and a small shack to store boating supplies.",
        1),
    BOAT_HOUSE_II(
        "Boat House II",
        "The boat is armored and has more cargo capacity.",
        1),
    HIDDEN_LAIR(
        "Hidden Lair",
        "Your lair has a secret location and is disguised to hide it from view. If discovered, use two downtime activities and pay coin equal to your Tier to relocate it and hide it once again.",
        1),
    QUARTERS(
        "Quarters",
        "Your lair includes living quarters for the crew. Without this upgrade, each PC sleeps elsewhere, and is vulnerable when they do so.",
        1),
    SECURE_LAIR(
        "Secure Lair",
        "Your lair has locks, alarms, and traps to thwart intruders.",
        1),
    SECURE_LAIR_II(
        "Secure Lair II",
        "Arcane measures that work against spirits. You might roll your crew's Tier to see how well they thwart an intruder.",
        1),
    VAULT(
        "Vault",
        "Your lair has a secure vault, increasing coin storage capacity to 8. A separate part can be used as a holding cell.",
        1),
    VAULT_II(
        "Vault II",
        "Your vault increases coin storage capacity to 16.",
        1),
    WORKSHOP(
        "Workshop",
        "A workshop with tools for tinkering and alchemy, plus a small library of books, documents, and maps. You may accomplish long-term projects with these assets without leaving your lair.",
        1),
    COHORT(
        "Cohort",
        "A gang or a single expert NPC who works for your crew. Spend two upgrades and create them as a gang or expert.",
        2),
    MASTERY(
        "Mastery",
        "Master-level training. You may advance PC action ratings to 4 (otherwise they are capped at 3).",
        4),
    QUALITY_DOCUMENTS(
        "Quality: Documents",
        "Improves the quality rating of all the PCs' documents beyond the crew's Tier and fine items.",
        1),
    QUALITY_GEAR(
        "Quality: Gear",
        "Improves burglary gear and climbing gear beyond the crew's Tier and fine items.",
        1),
    QUALITY_ARCANE_IMPLEMENTS(
        "Quality: Arcane Implements",
        "Improves the quality rating of all the PCs' arcane implements beyond the crew's Tier and fine items.",
        1),
    QUALITY_SUBTERFUGE_SUPPLIES(
        "Quality: Subterfuge Supplies",
        "Improves the quality rating of all the PCs' subterfuge supplies beyond the crew's Tier and fine items.",
        1),
    QUALITY_TOOLS(
        "Quality: Tools",
        "Improves demolition tools and tinkering tools beyond the crew's Tier and fine items.",
        1),
    QUALITY_WEAPONS(
        "Quality: Weapons",
        "Improves the quality rating of all the PCs' weapons beyond the crew's Tier and fine items.",
        1),
    TRAINING_INSIGHT(
        "Insight Training",
        "When you train Insight during downtime, mark 2 xp on the Insight track instead of 1.",
        1),
    TRAINING_PROWESS(
        "Prowess Training",
        "When you train Prowess during downtime, mark 2 xp on the Prowess track instead of 1.",
        1),
    TRAINING_RESOLVE(
        "Resolve Training",
        "When you train Resolve during downtime, mark 2 xp on the Resolve track instead of 1.",
        1),
    TRAINING_PLAYBOOK(
        "Playbook Training",
        "When you train during downtime, mark 2 xp on your playbook xp track instead of 1.",
        1),

    // --- Crew-type upgrades (pp. 99–119) ---
    ASSASSIN_RIGGING(
        "Assassin Rigging",
        "You get 2 free load worth of weapon or gear items.",
        1),
    BRAVOS_RIGGING(
        "Bravos Rigging",
        "You get 2 free load worth of weapon or armor items.",
        1),
    CULT_RIGGING(
        "Cult Rigging",
        "You get 2 free load worth of document or implement items.",
        1),
    HAWKER_RIGGING(
        "Hawker Rigging",
        "One carried item is concealed and has no load.",
        1),
    THIEF_RIGGING(
        "Thief Rigging",
        "You get 2 free load worth of tool or gear items.",
        1),
    SMUGGLER_RIGGING(
        "Smuggler Rigging",
        "Two of your carried items are perfectly concealed.",
        1),
    IRONHOOK_CONTACTS(
        "Ironhook Contacts",
        "Your Tier is effectively +1 higher in prison, including the incarceration roll.",
        1),
    ELITE_SKULKS(
        "Elite Skulks",
        "All of your cohorts with the Skulks type get +1d to quality rolls for Skulk-related actions.",
        1),
    ELITE_THUGS(
        "Elite Thugs",
        "All of your cohorts with the Thugs type get +1d to quality rolls for Thug-related actions.",
        1),
    ELITE_ROVERS(
        "Elite Rovers",
        "All of your cohorts with the Rovers type get +1d to quality rolls for Rover-related actions.",
        1),
    ELITE_ADEPTS(
        "Elite Adepts",
        "All of your cohorts with the Adepts type get +1d to quality rolls for Adept-related actions.",
        1),
    ELITE_ROOKS(
        "Elite Rooks",
        "All of your cohorts with the Rooks type get +1d to quality rolls for Rook-related actions.",
        1),
    HARDENED(
        "Hardened",
        "Each PC gets +1 trauma box. This costs three upgrades to unlock, not just one.",
        3),
    ORDAINED(
        "Ordained",
        "Each PC gets +1 trauma box. This costs three upgrades to unlock, not just one.",
        3),
    COMPOSED(
        "Composed",
        "Each PC gets +1 stress box. This costs three upgrades to unlock, not just one.",
        3),
    STEADY(
        "Steady",
        "Each PC gets +1 stress box. This costs three upgrades to unlock, not just one.",
        3),
    RITUAL_SANCTUM_IN_LAIR(
        "Ritual Sanctum in Lair",
        "This counts as a sacred and arcane workshop for occult practices and rituals.",
        1),
    UNDERGROUND_MAPS_AND_PASSKEYS(
        "Underground Maps & Passkeys",
        "You have easy passage through the underground canals, tunnels, and basements of the city.",
        1),
    CAMOUFLAGE(
        "Camouflage",
        "Your vehicles are perfectly concealed when at rest. They blend in as part of the environment, or as an uninteresting civilian vehicle.",
        1),
    BARGE(
        "Barge",
        "Add mobility to your lair. You can move it to a new location as a downtime activity.",
        1);

    private final String name;
    private final String description;
    private final int cost;

    UpgradeEnum(String name, String description, int cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
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
    public int getCost() {
        return cost;
    }

}
