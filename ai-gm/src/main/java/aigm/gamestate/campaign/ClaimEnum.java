package aigm.gamestate.campaign;

/** Crew claims from Core Rulebook pp. 99–119. Turf and lair are tracked on the crew, not here. */
public enum ClaimEnum implements Claim {

    // --- Shared ---
    INFORMANTS(
        "Informants",
        "Your eyes and ears on the streets are always on the lookout for new targets.",
        "+1d to gather information for a score."),
    INFIRMARY(
        "Infirmary",
        "Beds for long-term convalescence and a place to treat the wounded.",
        "+1d to healing treatment rolls."),
    VICE_DEN(
        "Vice Den",
        "A den where illicit indulgences are sold.",
        "During downtime, roll dice equal to your Tier. Earn coin equal to the highest result, minus your heat."),
    HAGFISH_FARM(
        "Hagfish Farm",
        "Quiet, convenient disposal for corpses left on the job.",
        "When you reduce heat after a score that involves killing, take +1d to the roll."),
    COVER_IDENTITIES(
        "Cover Identities",
        "False papers and assumed lives that confuse the opposition.",
        "+1d to the engagement roll for deception and social plans."),
    COVER_OPERATION(
        "Cover Operation",
        "A legitimate front that deflects attention from law enforcement.",
        "−2 heat per score."),
    LOOKOUTS(
        "Lookouts",
        "Watchers posted on your turf.",
        "+1d to Hunt or Survey on your turf."),
    TAVERN(
        "Tavern",
        "Booze and friendly conversation on-site.",
        "+1d to Consort and Sway rolls on-site."),
    WAREHOUSES(
        "Warehouses",
        "Space to hold spoils, cargo, and supplies.",
        "+1d to acquire asset rolls."),
    ANCIENT_GATE(
        "Ancient Gate",
        "A passage into the deathlands prepared so spirits will not molest you.",
        "Safe passage in the deathlands unless you directly provoke the spirits."),
    PROTECTION_RACKET(
        "Protection Racket",
        "Locals terrified of you gladly pay for \"protection.\"",
        "During downtime, roll dice equal to your Tier. Earn coin equal to the highest result, minus your heat."),

    // --- Assassins ---
    TRAINING_ROOMS(
        "Training Rooms",
        "Extra training space for your Skulks.",
        "Your Skulks cohorts get +1 scale."),
    FIXER(
        "Fixer",
        "A well-respected agent who arranges better payoffs from poorer clients.",
        "+2 coin in payoff for scores that involve lower-class clients."),
    VICTIM_TROPHIES(
        "Victim Trophies",
        "Word of your grisly collection gets around.",
        "+1 rep per score."),
    ENVOY(
        "Envoy",
        "A well-connected liaison who arranges better payoffs from rich clients.",
        "+2 coin in payoff for scores that involve high-class clients."),
    CITY_RECORDS(
        "City Records",
        "Blueprints and documents for a good approach.",
        "+1d to the engagement roll for stealth plans."),

    // --- Bravos ---
    BARRACKS(
        "Barracks",
        "Extra room means more gang members.",
        "Your Thug cohorts get +1 scale."),
    TERRORIZED_CITIZENS(
        "Terrorized Citizens",
        "Frightened locals offer tribute so they aren't next.",
        "+2 coin in payoff for scores that involve battle or extortion."),
    FIGHTING_PITS(
        "Fighting Pits",
        "Locals gamble on the blood-sports you host.",
        "During downtime, roll dice equal to your Tier. Earn coin equal to the highest result, minus your heat."),
    BLUECOAT_INTIMIDATION(
        "Bluecoat Intimidation",
        "The law doesn't want trouble from you; they look the other way.",
        "−2 heat per score."),
    STREET_FENCE(
        "Street Fence",
        "An expert who finds the treasure amid the trash you loot.",
        "+2 coin in payoff for scores that involve lower-class targets."),
    BLUECOAT_CONFEDERATES(
        "Bluecoat Confederates",
        "Street patrols around here help you out now.",
        "+1d to the engagement roll for assault plans."),

    // --- Cult ---
    CLOISTER(
        "Cloister",
        "Room for hopeful novices desperate to pledge their service.",
        "Your Adept cohorts get +1 scale."),
    OFFERTORY(
        "Offertory",
        "Frightened locals offer tribute when you perform your dark practices.",
        "+2 coin in payoff for scores that involve occult operations."),
    ANCIENT_OBELISK(
        "Ancient Obelisk",
        "Its power aids the cult so long as the deity is well pleased.",
        "−1 stress cost for all arcane powers and rituals, anywhere."),
    ANCIENT_TOWER(
        "Ancient Tower",
        "Pre-cataclysm sorcery focusing eldritch energy across the black mirror.",
        "+1d to Consort with arcane entities on-site."),
    SPIRIT_WELL(
        "Spirit Well",
        "A well that draws ghosts and other things to its power.",
        "+1d to Attune rolls on-site."),
    SANCTUARY(
        "Sanctuary",
        "Holds its effect as long as your deity is well-pleased with your service.",
        "+1d to Command and Sway rolls on-site."),
    SACRED_NEXUS(
        "Sacred Nexus",
        "Ancient arcane energy seeps into the wounded here.",
        "+1d to healing treatment rolls."),
    ANCIENT_ALTAR(
        "Ancient Altar",
        "Its blessing is with you.",
        "+1d to the engagement roll for occult plans."),

    // --- Hawkers ---
    PERSONAL_CLOTHIER(
        "Personal Clothier",
        "You always arrive in the most current and alluring fashion.",
        "+1d to the engagement roll for social plans."),
    LOCAL_GRAFT(
        "Local Graft",
        "A few city officials share bribe money with those who show they're players.",
        "+2 coin in payoff for scores that involve a show of force or socializing."),
    LUXURY_VENUE(
        "Luxury Venue",
        "Silks, paintings, and crystal impress the clientele.",
        "+1d to Consort and Sway rolls on-site."),
    FOREIGN_MARKET(
        "Foreign Market",
        "Some of your product makes its way out of the city.",
        "During downtime, roll dice equal to your Tier. Earn coin equal to the highest result, minus your heat."),
    SURPLUS_CACHE(
        "Surplus Cache",
        "An abundance of product that pads your pockets now and then.",
        "+2 coin in payoff for scores that involve product sale or supply."),

    // --- Shadows ---
    INTERROGATION_CHAMBER(
        "Interrogation Chamber",
        "Grisly business, but effective.",
        "+1d to Command and Sway on-site."),
    LOYAL_FENCE(
        "Loyal Fence",
        "A skilled eye and good contacts to move stolen goods.",
        "+2 coin in payoff for scores that involve burglary or robbery."),
    GAMBLING_DEN(
        "Gambling Den",
        "Cards, dice, or something more unusual.",
        "During downtime, roll dice equal to your Tier. Earn coin equal to the highest result, minus your heat."),
    DRUG_DEN(
        "Drug Den",
        "A den selling the drug of your choice.",
        "During downtime, roll dice equal to your Tier. Earn coin equal to the highest result, minus your heat."),
    COVERT_DROP(
        "Covert Drop",
        "The perfect hidden exchange point for discerning clientele.",
        "+2 coin in payoff for scores that involve espionage or sabotage."),
    SECRET_PATHWAYS(
        "Secret Pathways",
        "Forgotten canals, rooftop walkways, or another route of your choosing.",
        "+1d to the engagement roll for stealth plans."),

    // --- Smugglers ---
    SIDE_BUSINESS(
        "Side Business",
        "A legitimate business that pays you in secret.",
        "During downtime, roll dice equal to your Tier. Earn coin equal to the highest result, minus your heat."),
    LUXURY_FENCE(
        "Luxury Fence",
        "A skilled eye and good contacts to move hot luxury goods.",
        "+2 coin in payoff for scores that involve high-class targets."),
    FLEET(
        "Fleet",
        "Your cohorts have their own vehicles.",
        "Each cohort has a common vehicle with quality equal to your Tier."),
    SECRET_ROUTES(
        "Secret Routes",
        "Forgotten canals, hidden streets, or another route of your choosing.",
        "+1d to the engagement roll for transport plans.");

    private final String name;
    private final String description;
    private final String perk;

    ClaimEnum(String name, String description, String perk) {
        this.name = name;
        this.description = description;
        this.perk = perk;
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
    public String getPerk() {
        return perk;
    }

}
