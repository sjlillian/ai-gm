package aigm.gamestate.campaign;

import aigm.gamestate.Ability;

/** Crew special abilities from Core Rulebook pp. 98–121. */
public enum CrewAbilityEnum implements Ability {

    // --- Assassins (p. 101) ---
    DEADLY(
        "Deadly",
        "Each PC may add +1 action rating to Hunt, Prowl, or Skirmish (up to a max rating of 3)."),
    CROWS_VEIL(
        "Crow's Veil",
        "Your activities are hidden from the notice of the deathseeker crows. You don't take extra heat when killing is involved on a score."),
    EMBERDEATH(
        "Emberdeath",
        "Take 3 stress when you kill a victim to destroy their spirit at the moment of death, disintegrating body and spirit in sparking embers."),
    NO_TRACES(
        "No Traces",
        "When you keep an operation quiet or make it look like an accident, you get half the rep value of the target (round up) instead of zero. When you end downtime with zero heat, take +1 rep."),
    PATRON(
        "Patron",
        "When you advance your Tier, it costs half the coin it normally would."),
    PREDATORS(
        "Predators",
        "When you use a stealth or deception plan to commit murder, take +1d to the engagement roll."),
    VIPERS(
        "Vipers",
        "When you acquire or craft poisons, you get +1 result level to your roll. When you employ a poison, you are specially prepared to be immune to its effects."),

    // --- Bravos (p. 105) ---
    DANGEROUS(
        "Dangerous",
        "Each PC may add +1 action rating to Hunt, Skirmish, or Wreck (up to a max rating of 3)."),
    BLOOD_BROTHERS(
        "Blood Brothers",
        "When you fight alongside your cohorts in combat, they get +1d for teamwork rolls (setup and group actions). All of your cohorts get the Thugs type for free."),
    DOOR_KICKERS(
        "Door Kickers",
        "When you execute an assault plan, take +1d to the engagement roll."),
    FIENDS(
        "Fiends",
        "Fear is as good as respect. You may count each wanted level as if it were turf."),
    FORGED_IN_THE_FIRE(
        "Forged in the Fire",
        "Each PC has been toughened by cruel experience. You get +1d to resistance rolls."),
    WAR_DOGS(
        "War Dogs",
        "When you're at war (−3 faction status), PCs get +1d to vice rolls and still get two downtime activities, instead of just one."),

    // --- Cult (p. 109) ---
    CHOSEN(
        "Chosen",
        "Each PC may add +1 action rating to Attune, Study, or Sway (up to a max rating of 3)."),
    ANOINTED(
        "Anointed",
        "You gain +1d to resistance rolls against supernatural threats. You get +1d to healing rolls when you have supernatural harm."),
    BOUND_IN_DARKNESS(
        "Bound in Darkness",
        "You may use teamwork maneuvers with any cult member, regardless of the distance separating you. By taking 1 stress, your whispered message is heard by every cultist."),
    CONVICTION(
        "Conviction",
        "Each PC gains an additional vice: Worship. When you indulge this vice and bring a pleasing sacrifice, you don't overindulge if you clear excess stress. Your deity will also assist any one action roll you make until you indulge this vice again."),
    GLORY_INCARNATE(
        "Glory Incarnate",
        "Your deity sometimes manifests in the physical world. This can be a great boon, but the priorities and values of a god are not those of mortals."),
    SEALED_IN_BLOOD(
        "Sealed in Blood",
        "Each human sacrifice yields −3 stress cost for any ritual you perform."),
    ZEALOTRY(
        "Zealotry",
        "Your cohorts have abandoned their reason to devote themselves to the cult. They will undertake any service, no matter how dangerous or strange. They gain +1d to rolls when they act against enemies of the faith."),

    // --- Hawkers (p. 113) ---
    SILVER_TONGUES(
        "Silver Tongues",
        "Each PC may add +1 action rating to Command, Consort, or Sway (up to a max rating of 3)."),
    ACCORD(
        "Accord",
        "Sometimes friends are as good as territory. You may treat up to three +3 faction statuses you hold as if they are turf."),
    THE_GOOD_STUFF(
        "The Good Stuff",
        "Your merchandise is exquisite. The product quality is equal to your Tier +2. When you deal with a crew or faction, the GM will tell you who among them is hooked on your product."),
    GHOST_MARKET(
        "Ghost Market",
        "Through arcane ritual or hard-won experience, you have discovered how to prepare your product for sale to ghosts and/or demons. They do not pay in coin."),
    HIGH_SOCIETY(
        "High Society",
        "It's all about who you know. Take −1 heat during downtime and +1d to gather information about the city's elite."),
    HOOKED(
        "Hooked",
        "Your gang members use your product. Add the savage, unreliable, or wild flaw to your gangs to give them +1 quality (max rating of 4)."),

    // --- Shadows (p. 117) ---
    EVERYONE_STEALS(
        "Everyone Steals",
        "Each PC may add +1 action rating to Prowl, Finesse, or Tinker (up to a max rating of 3)."),
    GHOST_ECHOES(
        "Ghost Echoes",
        "All crew members can see and interact with the ghostly structures, streets, and objects within the echo of Doskvol that exists in the ghost field."),
    PACK_RATS(
        "Pack Rats",
        "Your lair is a jumble of stolen items. When you roll to acquire an asset, take +1d."),
    SECOND_STORY(
        "Second Story",
        "When you execute a clandestine infiltration, you get +1d to the engagement roll."),
    SLIPPERY(
        "Slippery",
        "When you roll entanglements, roll twice and keep the one you want. When you reduce heat on the crew, take +1d."),
    SYNCHRONIZED(
        "Synchronized",
        "When you perform a group action, you may count multiple 6s from different rolls as a critical success."),

    // --- Smugglers (p. 121) ---
    LIKE_PART_OF_THE_FAMILY(
        "Like Part of the Family",
        "Create one of your vehicles as a cohort (quality equal to your Tier +1). If the vehicle is upgraded (two boxes), it also gets armor. It can use teamwork actions using quality for rolls."),
    ALL_HANDS(
        "All Hands",
        "During downtime, one of your cohorts may perform a downtime activity for the crew to acquire an asset, reduce heat, or work on a long-term project."),
    GHOST_PASSAGE(
        "Ghost Passage",
        "All crew members are immune to possession by spirits, but may choose to carry a ghost as a passenger within their body."),
    JUST_PASSING_THROUGH(
        "Just Passing Through",
        "During downtime, take −1 heat. When your heat is 4 or less, you get +1d to deceive people when you pass yourselves off as ordinary citizens."),
    LEVERAGE(
        "Leverage",
        "Your crew supplies illicit goods for other factions. Whenever you gain rep, gain +1 rep."),
    REAVERS(
        "Reavers",
        "When you go into conflict aboard a vehicle, you gain +1 effect for vehicle damage and speed. Your vehicle gains armor."),
    RENEGADES(
        "Renegades",
        "Each PC may add +1 action rating to Finesse, Prowl, or Skirmish (up to a max rating of 3).");

    private final String name;
    private final String description;

    CrewAbilityEnum(String name, String description) {
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
