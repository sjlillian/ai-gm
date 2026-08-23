package aigm.gamestate.player;

import java.util.List;
import java.util.Map;

import aigm.gamestate.Ability;
import aigm.gamestate.Contact;

/**
 * Core Rulebook playbooks (pp. 61–87). Homebrew uses {@link PlaybookCustom}, not a sentinel here.
 * Starting action ratings are the two dots printed on the sheet; players add four more at creation.
 */
public enum PlaybookEnum implements Playbook {

    CUTTER(
        "Cutter",
        Map.of(Action.SKIRMISH, 2, Action.COMMAND, 1),
        List.of(
            PlayerAbilityEnum.BATTLEBORN,
            PlayerAbilityEnum.BODYGUARD,
            PlayerAbilityEnum.GHOST_FIGHTER,
            PlayerAbilityEnum.LEADER,
            PlayerAbilityEnum.MULE,
            PlayerAbilityEnum.NOT_TO_BE_TRIFLED_WITH,
            PlayerAbilityEnum.SAVAGE,
            PlayerAbilityEnum.VIGOROUS),
        List.of(
            ItemEnum.FINE_HAND_WEAPON,
            ItemEnum.FINE_HEAVY_WEAPON,
            ItemEnum.SCARY_WEAPON_OR_TOOL,
            ItemEnum.MANACLES_AND_CHAIN,
            ItemEnum.RAGE_ESSENCE_VIAL),
        List.of(
            PlayerContactEnum.CUTTER_MARLANE,
            PlayerContactEnum.CUTTER_CHAEL,
            PlayerContactEnum.CUTTER_MERCY,
            PlayerContactEnum.CUTTER_GRACE,
            PlayerContactEnum.CUTTER_SAWTOOTH),
        List.of(
            "You addressed a challenge with violence or coercion.",
            "You expressed your beliefs, drives, heritage, or background.",
            "You struggled with issues from your vice or traumas.")),

    HOUND(
        "Hound",
        Map.of(Action.HUNT, 2, Action.SURVEY, 1),
        List.of(
            PlayerAbilityEnum.SHARPSHOOTER,
            PlayerAbilityEnum.FOCUSED,
            PlayerAbilityEnum.GHOST_HUNTER,
            PlayerAbilityEnum.SCOUT,
            PlayerAbilityEnum.SURVIVOR,
            PlayerAbilityEnum.TOUGH_AS_NAILS,
            PlayerAbilityEnum.VENGEFUL),
        List.of(
            ItemEnum.FINE_PAIR_OF_PISTOLS,
            ItemEnum.FINE_LONG_RIFLE,
            ItemEnum.ELECTROPLASMIC_AMMUNITION,
            ItemEnum.TRAINED_HUNTING_PET,
            ItemEnum.SPYGLASS),
        List.of(
            PlayerContactEnum.HOUND_STEINER,
            PlayerContactEnum.HOUND_CELENE,
            PlayerContactEnum.HOUND_MELVIR,
            PlayerContactEnum.HOUND_VELERIS,
            PlayerContactEnum.HOUND_CASTA),
        List.of(
            "You addressed a challenge with tracking or violence.",
            "You expressed your beliefs, drives, heritage, or background.",
            "You struggled with issues from your vice or traumas.")),

    LEECH(
        "Leech",
        Map.of(Action.TINKER, 2, Action.WRECK, 1),
        List.of(
            PlayerAbilityEnum.ALCHEMIST,
            PlayerAbilityEnum.ARTIFICER,
            PlayerAbilityEnum.ANALYST,
            PlayerAbilityEnum.FORTITUDE,
            PlayerAbilityEnum.GHOST_WARD,
            PlayerAbilityEnum.PHYSICKER,
            PlayerAbilityEnum.SABOTEUR,
            PlayerAbilityEnum.VENOMOUS),
        List.of(
            ItemEnum.FINE_TINKERING_TOOLS,
            ItemEnum.FINE_WRECKER_TOOLS,
            ItemEnum.BLOWGUN_AND_DARTS,
            ItemEnum.BANDOLIER_OF_ALCHEMICALS,
            ItemEnum.GADGETS),
        List.of(
            PlayerContactEnum.LEECH_STAZIA,
            PlayerContactEnum.LEECH_VELDREN,
            PlayerContactEnum.LEECH_ECKERD,
            PlayerContactEnum.LEECH_JUL,
            PlayerContactEnum.LEECH_MALISTA),
        List.of(
            "You addressed a challenge with technical skill or mayhem.",
            "You expressed your beliefs, drives, heritage, or background.",
            "You struggled with issues from your vice or traumas.")),

    LURK(
        "Lurk",
        Map.of(Action.PROWL, 2, Action.FINESSE, 1),
        List.of(
            PlayerAbilityEnum.INFILTRATOR,
            PlayerAbilityEnum.AMBUSH,
            PlayerAbilityEnum.DAREDEVIL,
            PlayerAbilityEnum.THE_DEVILS_FOOTSTEPS,
            PlayerAbilityEnum.EXPERTISE,
            PlayerAbilityEnum.GHOST_VEIL,
            PlayerAbilityEnum.REFLEXES,
            PlayerAbilityEnum.SHADOW),
        List.of(
            ItemEnum.FINE_LOCKPICKS,
            ItemEnum.FINE_SHADOW_CLOAK,
            ItemEnum.LIGHT_CLIMBING_GEAR,
            ItemEnum.SILENCE_POTION_VIAL,
            ItemEnum.DARK_SIGHT_GOGGLES),
        List.of(
            PlayerContactEnum.LURK_TELDA,
            PlayerContactEnum.LURK_DARMOT,
            PlayerContactEnum.LURK_FRAKE,
            PlayerContactEnum.LURK_ROSLYN_KELLIS,
            PlayerContactEnum.LURK_PETRA),
        List.of(
            "You addressed a challenge with stealth or evasion.",
            "You expressed your beliefs, drives, heritage, or background.",
            "You struggled with issues from your vice or traumas.")),

    SLIDE(
        "Slide",
        Map.of(Action.CONSORT, 1, Action.SWAY, 2),
        List.of(
            PlayerAbilityEnum.ROOKS_GAMBIT,
            PlayerAbilityEnum.CLOAK_AND_DAGGER,
            PlayerAbilityEnum.GHOST_VOICE,
            PlayerAbilityEnum.A_LITTLE_SOMETHING_ON_THE_SIDE,
            PlayerAbilityEnum.LIKE_LOOKING_INTO_A_MIRROR,
            PlayerAbilityEnum.MESMERISM,
            PlayerAbilityEnum.SUBTERFUGE,
            PlayerAbilityEnum.TRUST_IN_ME),
        List.of(
            ItemEnum.FINE_CLOTHES_AND_JEWELRY,
            ItemEnum.FINE_DISGUISE_KIT,
            ItemEnum.FINE_LOADED_DICE,
            ItemEnum.TRANCE_POWDER,
            ItemEnum.CANE_SWORD),
        List.of(
            PlayerContactEnum.SLIDE_BRYL,
            PlayerContactEnum.SLIDE_BAZSO_BAZ,
            PlayerContactEnum.SLIDE_KLYRA,
            PlayerContactEnum.SLIDE_NYRIX,
            PlayerContactEnum.SLIDE_HARKER),
        List.of(
            "You addressed a challenge with deception or influence.",
            "You expressed your beliefs, drives, heritage, or background.",
            "You struggled with issues from your vice or traumas.")),

    SPIDER(
        "Spider",
        Map.of(Action.CONSORT, 2, Action.STUDY, 1),
        List.of(
            PlayerAbilityEnum.FORESIGHT,
            PlayerAbilityEnum.CALCULATING,
            PlayerAbilityEnum.CONNECTED,
            PlayerAbilityEnum.FUNCTIONING_VICE,
            PlayerAbilityEnum.GHOST_CONTRACT,
            PlayerAbilityEnum.JAIL_BIRD,
            PlayerAbilityEnum.MASTERMIND,
            PlayerAbilityEnum.WEAVING_THE_WEB),
        List.of(
            ItemEnum.FINE_COVER_IDENTITY,
            ItemEnum.FINE_BOTTLE_OF_WHISKEY,
            ItemEnum.BLUEPRINTS,
            ItemEnum.VIAL_OF_SLUMBER_ESSENCE,
            ItemEnum.CONCEALED_PALM_PISTOL),
        List.of(
            PlayerContactEnum.SPIDER_SALIA,
            PlayerContactEnum.SPIDER_AUGUS,
            PlayerContactEnum.SPIDER_JENNAH,
            PlayerContactEnum.SPIDER_RIVEN,
            PlayerContactEnum.SPIDER_JEREN),
        List.of(
            "You addressed a challenge with calculation or conspiracy.",
            "You expressed your beliefs, drives, heritage, or background.",
            "You struggled with issues from your vice or traumas.")),

    WHISPER(
        "Whisper",
        Map.of(Action.ATTUNE, 2, Action.STUDY, 1),
        List.of(
            PlayerAbilityEnum.COMPEL,
            PlayerAbilityEnum.GHOST_MIND,
            PlayerAbilityEnum.IRON_WILL,
            PlayerAbilityEnum.OCCULTIST,
            PlayerAbilityEnum.RITUAL,
            PlayerAbilityEnum.STRANGE_METHODS,
            PlayerAbilityEnum.TEMPEST,
            PlayerAbilityEnum.WARDED),
        List.of(
            ItemEnum.FINE_LIGHTNING_HOOK,
            ItemEnum.FINE_SPIRIT_MASK,
            ItemEnum.SPIRIT_BOTTLES,
            ItemEnum.GHOST_KEY,
            ItemEnum.DEMONBANE_CHARM),
        List.of(
            PlayerContactEnum.WHISPER_NYRYX,
            PlayerContactEnum.WHISPER_SCURLOCK,
            PlayerContactEnum.WHISPER_SETARRA,
            PlayerContactEnum.WHISPER_QUELLYN,
            PlayerContactEnum.WHISPER_FLINT),
        List.of(
            "You addressed a challenge with knowledge or arcane power.",
            "You expressed your beliefs, drives, heritage, or background.",
            "You struggled with issues from your vice or traumas."));

    private final String name;
    private final Map<Action, Integer> startingActionRatings;
    private final List<Ability> availableAbilities;
    private final List<Item> availableItems;
    private final List<Contact> availableContacts;
    private final List<String> xpTriggers;

    PlaybookEnum(
            String name,
            Map<Action, Integer> startingActionRatings,
            List<Ability> availableAbilities,
            List<Item> availableItems,
            List<Contact> availableContacts,
            List<String> xpTriggers) {
        this.name = name;
        this.startingActionRatings = startingActionRatings;
        this.availableAbilities = availableAbilities;
        this.availableItems = availableItems;
        this.availableContacts = availableContacts;
        this.xpTriggers = xpTriggers;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<Action, Integer> getStartingActionRatings() {
        return startingActionRatings;
    }

    @Override
    public List<Ability> getAvailableAbilities() {
        return availableAbilities;
    }

    @Override
    public List<Item> getAvailableItems() {
        return availableItems;
    }

    @Override
    public List<Contact> getAvailableContacts() {
        return availableContacts;
    }

    @Override
    public List<String> getXpTriggers() {
        return xpTriggers;
    }

}
