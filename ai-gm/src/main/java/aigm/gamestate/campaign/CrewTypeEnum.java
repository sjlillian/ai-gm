package aigm.gamestate.campaign;

import java.util.List;

import aigm.gamestate.Ability;
import aigm.gamestate.Contact;

/**
 * Core Rulebook crew types (pp. 98–121). {@link #getUpgrades()} is starting
 * upgrades plus that type's unique upgrades. Homebrew uses {@link CrewTypeCustom}, not a sentinel here.
 */
public enum CrewTypeEnum implements CrewType {

    ASSASSINS(
        "Assassins",
        "When you execute a successful accident, disappearance, murder, or ransom operation.",
        List.of(
            UpgradeEnum.TRAINING_INSIGHT,
            UpgradeEnum.TRAINING_PROWESS,
            UpgradeEnum.ASSASSIN_RIGGING,
            UpgradeEnum.IRONHOOK_CONTACTS,
            UpgradeEnum.ELITE_SKULKS,
            UpgradeEnum.ELITE_THUGS,
            UpgradeEnum.HARDENED),
        List.of(
            CrewAbilityEnum.DEADLY,
            CrewAbilityEnum.CROWS_VEIL,
            CrewAbilityEnum.EMBERDEATH,
            CrewAbilityEnum.NO_TRACES,
            CrewAbilityEnum.PATRON,
            CrewAbilityEnum.PREDATORS,
            CrewAbilityEnum.VIPERS),
        List.of(
            CrewContactEnum.ASSASSINS_TREV,
            CrewContactEnum.ASSASSINS_LYDRA,
            CrewContactEnum.ASSASSINS_IRIMINA,
            CrewContactEnum.ASSASSINS_KARLOS,
            CrewContactEnum.ASSASSINS_EXETER,
            CrewContactEnum.ASSASSINS_SEVOY),
        List.of(
            ClaimEnum.TRAINING_ROOMS,
            ClaimEnum.VICE_DEN,
            ClaimEnum.FIXER,
            ClaimEnum.INFORMANTS,
            ClaimEnum.HAGFISH_FARM,
            ClaimEnum.VICTIM_TROPHIES,
            ClaimEnum.COVER_OPERATION,
            ClaimEnum.INFIRMARY,
            ClaimEnum.PROTECTION_RACKET,
            ClaimEnum.ENVOY,
            ClaimEnum.COVER_IDENTITIES,
            ClaimEnum.CITY_RECORDS)),

    BRAVOS(
        "Bravos",
        "When you execute a successful battle, extortion, sabotage, or smash & grab operation.",
        List.of(
            UpgradeEnum.TRAINING_PROWESS,
            UpgradeEnum.COHORT,
            UpgradeEnum.BRAVOS_RIGGING,
            UpgradeEnum.IRONHOOK_CONTACTS,
            UpgradeEnum.ELITE_ROVERS,
            UpgradeEnum.ELITE_THUGS,
            UpgradeEnum.HARDENED),
        List.of(
            CrewAbilityEnum.DANGEROUS,
            CrewAbilityEnum.BLOOD_BROTHERS,
            CrewAbilityEnum.DOOR_KICKERS,
            CrewAbilityEnum.FIENDS,
            CrewAbilityEnum.FORGED_IN_THE_FIRE,
            CrewAbilityEnum.PATRON,
            CrewAbilityEnum.WAR_DOGS),
        List.of(
            CrewContactEnum.BRAVOS_MEG,
            CrewContactEnum.BRAVOS_CONWAY,
            CrewContactEnum.BRAVOS_KELLER,
            CrewContactEnum.BRAVOS_TOMAS,
            CrewContactEnum.BRAVOS_WALKER,
            CrewContactEnum.BRAVOS_LUTES),
        List.of(
            ClaimEnum.BARRACKS,
            ClaimEnum.TERRORIZED_CITIZENS,
            ClaimEnum.INFORMANTS,
            ClaimEnum.PROTECTION_RACKET,
            ClaimEnum.FIGHTING_PITS,
            ClaimEnum.INFIRMARY,
            ClaimEnum.BLUECOAT_INTIMIDATION,
            ClaimEnum.STREET_FENCE,
            ClaimEnum.BLUECOAT_CONFEDERATES,
            ClaimEnum.WAREHOUSES)),

    CULT(
        "Cult",
        "When you advance the agenda of your deity or embody its precepts in action.",
        List.of(
            UpgradeEnum.TRAINING_RESOLVE,
            UpgradeEnum.COHORT,
            UpgradeEnum.CULT_RIGGING,
            UpgradeEnum.RITUAL_SANCTUM_IN_LAIR,
            UpgradeEnum.ELITE_ADEPTS,
            UpgradeEnum.ELITE_THUGS,
            UpgradeEnum.ORDAINED),
        List.of(
            CrewAbilityEnum.CHOSEN,
            CrewAbilityEnum.ANOINTED,
            CrewAbilityEnum.BOUND_IN_DARKNESS,
            CrewAbilityEnum.CONVICTION,
            CrewAbilityEnum.GLORY_INCARNATE,
            CrewAbilityEnum.SEALED_IN_BLOOD,
            CrewAbilityEnum.ZEALOTRY),
        List.of(
            CrewContactEnum.CULT_GAGAN,
            CrewContactEnum.CULT_ADIKIN,
            CrewContactEnum.CULT_HUTCHINS,
            CrewContactEnum.CULT_MORIYA,
            CrewContactEnum.CULT_MATEAS_KLINE,
            CrewContactEnum.CULT_BENNETT),
        List.of(
            ClaimEnum.CLOISTER,
            ClaimEnum.VICE_DEN,
            ClaimEnum.OFFERTORY,
            ClaimEnum.ANCIENT_OBELISK,
            ClaimEnum.ANCIENT_TOWER,
            ClaimEnum.SPIRIT_WELL,
            ClaimEnum.SANCTUARY,
            ClaimEnum.SACRED_NEXUS,
            ClaimEnum.ANCIENT_ALTAR,
            ClaimEnum.ANCIENT_GATE)),

    HAWKERS(
        "Hawkers",
        "When you acquire new product supply, execute clandestine or covert sales, or secure new sales territory.",
        List.of(
            UpgradeEnum.TRAINING_RESOLVE,
            UpgradeEnum.SECURE_LAIR,
            UpgradeEnum.HAWKER_RIGGING,
            UpgradeEnum.IRONHOOK_CONTACTS,
            UpgradeEnum.ELITE_ROOKS,
            UpgradeEnum.ELITE_THUGS,
            UpgradeEnum.COMPOSED),
        List.of(
            CrewAbilityEnum.SILVER_TONGUES,
            CrewAbilityEnum.ACCORD,
            CrewAbilityEnum.THE_GOOD_STUFF,
            CrewAbilityEnum.GHOST_MARKET,
            CrewAbilityEnum.HIGH_SOCIETY,
            CrewAbilityEnum.HOOKED,
            CrewAbilityEnum.PATRON),
        List.of(
            CrewContactEnum.HAWKERS_ROLAN_WOTT,
            CrewContactEnum.HAWKERS_LAROZE,
            CrewContactEnum.HAWKERS_LYDRA,
            CrewContactEnum.HAWKERS_HOXLEY,
            CrewContactEnum.HAWKERS_ANYA,
            CrewContactEnum.HAWKERS_MARLO),
        List.of(
            ClaimEnum.PERSONAL_CLOTHIER,
            ClaimEnum.LOCAL_GRAFT,
            ClaimEnum.LOOKOUTS,
            ClaimEnum.INFORMANTS,
            ClaimEnum.LUXURY_VENUE,
            ClaimEnum.FOREIGN_MARKET,
            ClaimEnum.VICE_DEN,
            ClaimEnum.SURPLUS_CACHE,
            ClaimEnum.COVER_OPERATION,
            ClaimEnum.COVER_IDENTITIES)),

    SHADOWS(
        "Shadows",
        "When you execute a burglary, espionage, robbery, or sabotage operation.",
        List.of(
            UpgradeEnum.TRAINING_PROWESS,
            UpgradeEnum.HIDDEN_LAIR,
            UpgradeEnum.THIEF_RIGGING,
            UpgradeEnum.UNDERGROUND_MAPS_AND_PASSKEYS,
            UpgradeEnum.ELITE_ROOKS,
            UpgradeEnum.ELITE_SKULKS,
            UpgradeEnum.STEADY),
        List.of(
            CrewAbilityEnum.EVERYONE_STEALS,
            CrewAbilityEnum.GHOST_ECHOES,
            CrewAbilityEnum.PACK_RATS,
            CrewAbilityEnum.PATRON,
            CrewAbilityEnum.SECOND_STORY,
            CrewAbilityEnum.SLIPPERY,
            CrewAbilityEnum.SYNCHRONIZED),
        List.of(
            CrewContactEnum.SHADOWS_DOWLER,
            CrewContactEnum.SHADOWS_LAROZE,
            CrewContactEnum.SHADOWS_AMANCIO,
            CrewContactEnum.SHADOWS_FITZ,
            CrewContactEnum.SHADOWS_ADELAIDE_PHROAIG,
            CrewContactEnum.SHADOWS_RIGNEY),
        List.of(
            ClaimEnum.INTERROGATION_CHAMBER,
            ClaimEnum.LOYAL_FENCE,
            ClaimEnum.GAMBLING_DEN,
            ClaimEnum.TAVERN,
            ClaimEnum.DRUG_DEN,
            ClaimEnum.INFORMANTS,
            ClaimEnum.LOOKOUTS,
            ClaimEnum.HAGFISH_FARM,
            ClaimEnum.INFIRMARY,
            ClaimEnum.COVERT_DROP,
            ClaimEnum.SECRET_PATHWAYS)),

    SMUGGLERS(
        "Smugglers",
        "When you execute a smuggling operation or acquire new clients or contraband sources.",
        List.of(
            UpgradeEnum.TRAINING_PROWESS,
            UpgradeEnum.CARRIAGE_HOUSE,
            UpgradeEnum.BOAT_HOUSE,
            UpgradeEnum.SMUGGLER_RIGGING,
            UpgradeEnum.CAMOUFLAGE,
            UpgradeEnum.ELITE_ROVERS,
            UpgradeEnum.BARGE,
            UpgradeEnum.STEADY),
        List.of(
            CrewAbilityEnum.LIKE_PART_OF_THE_FAMILY,
            CrewAbilityEnum.ALL_HANDS,
            CrewAbilityEnum.GHOST_PASSAGE,
            CrewAbilityEnum.JUST_PASSING_THROUGH,
            CrewAbilityEnum.LEVERAGE,
            CrewAbilityEnum.REAVERS,
            CrewAbilityEnum.RENEGADES),
        List.of(
            CrewContactEnum.SMUGGLERS_ELYNN,
            CrewContactEnum.SMUGGLERS_ROLAN,
            CrewContactEnum.SMUGGLERS_SERA,
            CrewContactEnum.SMUGGLERS_NYELLE,
            CrewContactEnum.SMUGGLERS_DECKER,
            CrewContactEnum.SMUGGLERS_ESME),
        List.of(
            ClaimEnum.SIDE_BUSINESS,
            ClaimEnum.LUXURY_FENCE,
            ClaimEnum.VICE_DEN,
            ClaimEnum.TAVERN,
            ClaimEnum.ANCIENT_GATE,
            ClaimEnum.INFORMANTS,
            ClaimEnum.FLEET,
            ClaimEnum.SECRET_ROUTES,
            ClaimEnum.COVER_OPERATION,
            ClaimEnum.WAREHOUSES));

    private final String type;
    private final String xpTrigger;
    private final List<Upgrade> upgrades;
    private final List<Ability> abilities;
    private final List<Contact> contacts;
    private final List<Claim> claims;

    CrewTypeEnum(
            String type,
            String xpTrigger,
            List<Upgrade> upgrades,
            List<Ability> abilities,
            List<Contact> contacts,
            List<Claim> claims) {
        this.type = type;
        this.xpTrigger = xpTrigger;
        this.upgrades = upgrades;
        this.abilities = abilities;
        this.contacts = contacts;
        this.claims = claims;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getXPTrigger() {
        return xpTrigger;
    }

    @Override
    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    @Override
    public List<Ability> getAbilities() {
        return abilities;
    }

    @Override
    public List<Contact> getContacts() {
        return contacts;
    }

    @Override
    public List<Claim> getClaims() {
        return claims;
    }

    @Override
    public List<Upgrade> getStartingUpgrades() {
        return switch (this) {
            case ASSASSINS -> List.of(
                UpgradeEnum.TRAINING_INSIGHT, UpgradeEnum.TRAINING_PROWESS, UpgradeEnum.ASSASSIN_RIGGING);
            case BRAVOS -> List.of(UpgradeEnum.TRAINING_PROWESS, UpgradeEnum.COHORT);
            case CULT -> List.of(
                UpgradeEnum.TRAINING_RESOLVE, UpgradeEnum.COHORT, UpgradeEnum.RITUAL_SANCTUM_IN_LAIR);
            case HAWKERS -> List.of(
                UpgradeEnum.TRAINING_RESOLVE, UpgradeEnum.SECURE_LAIR, UpgradeEnum.HAWKER_RIGGING);
            case SHADOWS -> List.of(
                UpgradeEnum.TRAINING_PROWESS, UpgradeEnum.HIDDEN_LAIR, UpgradeEnum.THIEF_RIGGING);
            case SMUGGLERS -> List.of(UpgradeEnum.TRAINING_PROWESS);
        };
    }
}
