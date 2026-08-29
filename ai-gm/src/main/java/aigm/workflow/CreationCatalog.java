package aigm.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aigm.gamestate.Ability;
import aigm.gamestate.Contact;
import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.campaign.CrewType;
import aigm.gamestate.campaign.CrewTypeEnum;
import aigm.gamestate.campaign.Upgrade;
import aigm.gamestate.campaign.UpgradeEnum;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Background;
import aigm.gamestate.player.Heritage;
import aigm.gamestate.player.Playbook;
import aigm.gamestate.player.PlaybookEnum;
import aigm.gamestate.player.ViceKind;

/** Resolve Session 0 CLI tokens against catalog enums and playbook/crew lists. */
final class CreationCatalog {

    private CreationCatalog() {}

    static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.trim().toUpperCase(Locale.ROOT).replace(' ', '_').replace('-', '_').replace('\'', '_');
    }

    static PlaybookEnum playbook(String raw) {
        return enumValue(PlaybookEnum.class, raw, "playbook");
    }

    static Heritage heritage(String raw) {
        return enumValue(Heritage.class, raw, "heritage");
    }

    static Background background(String raw) {
        return enumValue(Background.class, raw, "background");
    }

    static Action action(String raw) {
        return enumValue(Action.class, raw, "action");
    }

    static ViceKind viceKind(String raw) {
        return enumValue(ViceKind.class, raw, "vice");
    }

    static CrewTypeEnum crewType(String raw) {
        return enumValue(CrewTypeEnum.class, raw, "crew type");
    }

    static CrewStanding.Reputation reputation(String raw) {
        return enumValue(CrewStanding.Reputation.class, raw, "reputation");
    }

    static <E extends Enum<E>> E enumValue(Class<E> type, String raw, String label) {
        String normalized = normalize(raw);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(label + " required");
        }
        try {
            return Enum.valueOf(type, normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown " + label + ": " + raw);
        }
    }

    static Ability ability(List<Ability> pool, String raw) {
        Ability found = findAbility(pool, raw);
        if (found == null) {
            throw new IllegalArgumentException("Unknown ability: " + raw);
        }
        return found;
    }

    static Contact contact(List<Contact> pool, String raw) {
        Contact found = findContact(pool, raw);
        if (found == null) {
            throw new IllegalArgumentException("Unknown contact: " + raw);
        }
        return found;
    }

    static Upgrade upgrade(List<Upgrade> pool, String raw) {
        Upgrade found = findUpgrade(pool, raw);
        if (found == null) {
            throw new IllegalArgumentException("Unknown upgrade: " + raw);
        }
        return found;
    }

    static Ability findAbility(List<Ability> pool, String raw) {
        String normalized = normalize(raw);
        for (Ability ability : pool) {
            if (normalize(ability.getName()).equals(normalized)
                || ability.getName().equalsIgnoreCase(raw == null ? "" : raw.trim())
                || (ability instanceof Enum<?> e && e.name().equals(normalized))) {
                return ability;
            }
        }
        return null;
    }

    static Contact findContact(List<Contact> pool, String raw) {
        String normalized = normalize(raw);
        for (Contact contact : pool) {
            if (normalize(contact.getName()).equals(normalized)
                || contact.getName().equalsIgnoreCase(raw == null ? "" : raw.trim())
                || (contact instanceof Enum<?> e && e.name().equals(normalized))) {
                return contact;
            }
        }
        return null;
    }

    static Upgrade findUpgrade(List<Upgrade> pool, String raw) {
        String normalized = normalize(raw);
        for (Upgrade upgrade : pool) {
            if (normalize(upgrade.getName()).equals(normalized)
                || upgrade.getName().equalsIgnoreCase(raw == null ? "" : raw.trim())
                || (upgrade instanceof Enum<?> e && e.name().equals(normalized))) {
                return upgrade;
            }
        }
        return null;
    }

    static List<String> playbookNames() {
        List<String> names = new ArrayList<>();
        for (PlaybookEnum playbook : PlaybookEnum.values()) {
            names.add(playbook.name());
        }
        return names;
    }

    static List<String> heritageNames() {
        List<String> names = new ArrayList<>();
        for (Heritage heritage : Heritage.values()) {
            names.add(heritage.name());
        }
        return names;
    }

    static List<String> backgroundNames() {
        List<String> names = new ArrayList<>();
        for (Background background : Background.values()) {
            names.add(background.name());
        }
        return names;
    }

    static List<String> actionNames() {
        List<String> names = new ArrayList<>();
        for (Action action : Action.values()) {
            names.add(action.name());
        }
        return names;
    }

    static List<String> viceNames() {
        List<String> names = new ArrayList<>();
        for (ViceKind kind : ViceKind.values()) {
            names.add(kind.name());
        }
        return names;
    }

    static List<String> crewTypeNames() {
        List<String> names = new ArrayList<>();
        for (CrewTypeEnum type : CrewTypeEnum.values()) {
            names.add(type.name());
        }
        return names;
    }

    static List<String> reputationNames() {
        List<String> names = new ArrayList<>();
        for (CrewStanding.Reputation reputation : CrewStanding.Reputation.values()) {
            names.add(reputation.name());
        }
        return names;
    }

    static List<String> abilityNames(List<Ability> pool) {
        List<String> names = new ArrayList<>();
        for (Ability ability : pool) {
            names.add(ability instanceof Enum<?> e ? e.name() : ability.getName());
        }
        return names;
    }

    static List<String> contactNames(List<Contact> pool) {
        List<String> names = new ArrayList<>();
        for (Contact contact : pool) {
            names.add(contact instanceof Enum<?> e ? e.name() : contact.getName());
        }
        return names;
    }

    static List<String> upgradeNames(List<Upgrade> pool) {
        List<String> names = new ArrayList<>();
        for (Upgrade upgrade : pool) {
            names.add(upgrade instanceof Enum<?> e ? e.name() : upgrade.getName());
        }
        return names;
    }

    static List<Upgrade> upgradePool(CrewType type) {
        List<Upgrade> pool = new ArrayList<>();
        if (type != null) {
            pool.addAll(type.getUpgrades());
        }
        for (UpgradeEnum upgrade : UpgradeEnum.values()) {
            if (findUpgrade(pool, upgrade.name()) == null) {
                pool.add(upgrade);
            }
        }
        return pool;
    }

    static List<String> playbookAbilityNames(Playbook playbook) {
        return playbook == null ? List.of() : abilityNames(playbook.getAvailableAbilities());
    }

    static List<String> playbookContactNames(Playbook playbook) {
        return playbook == null ? List.of() : contactNames(playbook.getAvailableContacts());
    }

    static List<PromptOption> playbookOptions() {
        List<PromptOption> options = new ArrayList<>();
        for (PlaybookEnum playbook : PlaybookEnum.values()) {
            options.add(new PromptOption(playbook.name(), playbook.getName(), playbook.getDescription()));
        }
        return options;
    }

    static List<PromptOption> heritageOptions() {
        List<PromptOption> options = new ArrayList<>();
        for (Heritage heritage : Heritage.values()) {
            options.add(new PromptOption(heritage.name(), heritage.getName(), heritage.getDescription()));
        }
        return options;
    }

    static List<PromptOption> backgroundOptions() {
        List<PromptOption> options = new ArrayList<>();
        for (Background background : Background.values()) {
            options.add(new PromptOption(background.name(), background.getName(), background.getDescription()));
        }
        return options;
    }

    static List<PromptOption> actionOptions() {
        List<PromptOption> options = new ArrayList<>();
        for (Action action : Action.values()) {
            options.add(new PromptOption(
                action.name(),
                action.name(),
                action.getAttribute().name() + ". " + action.getDescription()
            ));
        }
        return options;
    }

    static List<PromptOption> viceOptions() {
        List<PromptOption> options = new ArrayList<>();
        for (ViceKind kind : ViceKind.values()) {
            options.add(new PromptOption(kind.name(), kind.getName(), kind.getDescription()));
        }
        return options;
    }

    static List<PromptOption> vicePurveyorOptions(ViceKind kind) {
        return vicePurveyors(kind);
    }

    static List<PromptOption> vicePurveyors(ViceKind kind) {
        if (kind == null) {
            List<PromptOption> all = new ArrayList<>();
            for (ViceKind value : ViceKind.values()) {
                all.addAll(vicePurveyors(value));
            }
            return all;
        }
        return switch (kind) {
            case FAITH -> List.of(
                purveyor(ViceKind.FAITH, "Church of the Ecstasy", "Church of the Ecstasy of the Flesh, Six Towers — state religion, public rites."),
                purveyor(ViceKind.FAITH, "Forgotten Gods shrine", "A hidden cult chapel (Charterhall / Nightmarket) to a god the Imperium would rather erase."),
                purveyor(ViceKind.FAITH, "Weeping Lady", "Charhollow shrine of the Weeping Lady; charity with strings.")
            );
            case GAMBLING -> List.of(
                purveyor(ViceKind.GAMBLING, "The Hooded Fox", "Crow's Foot gambling hall — cards, dice, and knives under the table."),
                purveyor(ViceKind.GAMBLING, "Silver Stag", "Silkshore canal-side games for people who dress better than they should."),
                purveyor(ViceKind.GAMBLING, "Six Towers pits", "Blood sports and side-bets in the ruined courtyards of Six Towers."),
                purveyor(ViceKind.GAMBLING, "Nightmarket dens", "Back-room games behind Nightmarket stalls.")
            );
            case LUXURY -> List.of(
                purveyor(ViceKind.LUXURY, "The Veil", "Silkshore house of exquisite (and expensive) indulgences."),
                purveyor(ViceKind.LUXURY, "Brightstone salon", "A private Brightstone parlor that sells status as much as comfort."),
                purveyor(ViceKind.LUXURY, "Whitecrown gallery", "Art, wine, and the company of people who can ruin you politely.")
            );
            case OBLIGATION -> List.of(
                purveyor(ViceKind.OBLIGATION, "Charhollow family", "Kin in Charhollow who expect your coin, your time, or your violence."),
                purveyor(ViceKind.OBLIGATION, "Skovlan relief", "A Dunslough / Docks circle that still fights the last war with charity and grudges."),
                purveyor(ViceKind.OBLIGATION, "Dock union", "A Docks labor circle that calls in markers when the bosses squeeze.")
            );
            case PLEASURE -> List.of(
                purveyor(ViceKind.PLEASURE, "Red Lamp", "Silkshore pleasure house — music, lovers, and gossip."),
                purveyor(ViceKind.PLEASURE, "Nightmarket opium", "A Nightmarket den that sells forgetting by the pipe."),
                purveyor(ViceKind.PLEASURE, "The Hooded Fox", "Crow's Foot drink, dice, and company until dawn.")
            );
            case STUPOR -> List.of(
                purveyor(ViceKind.STUPOR, "Leaky Bucket", "Crow's Foot grog house; you will not remember the end of the night."),
                purveyor(ViceKind.STUPOR, "Coalridge gin", "Factory-floor liquor that sandblasts the mind."),
                purveyor(ViceKind.STUPOR, "Dunslough stills", "Rotgut in the shadow of Ironhook.")
            );
            case WEIRD -> List.of(
                purveyor(ViceKind.WEIRD, "Spirit well", "A Nightmarket well where the ghost field is thin."),
                purveyor(ViceKind.WEIRD, "Temple of Forgotten Gods", "Charterhall occultists who collect strange essences and stranger patrons."),
                purveyor(ViceKind.WEIRD, "Whisper-monger", "A Six Towers medium who brokers audiences with the dead.")
            );
        };
    }

    static List<PromptOption> crewTypeOptions() {
        List<PromptOption> options = new ArrayList<>();
        for (CrewTypeEnum type : CrewTypeEnum.values()) {
            options.add(new PromptOption(
                type.name(),
                type.getType(),
                "XP: " + type.getXPTrigger()
            ));
        }
        return options;
    }

    static List<PromptOption> reputationOptions() {
        List<PromptOption> options = new ArrayList<>();
        for (CrewStanding.Reputation reputation : CrewStanding.Reputation.values()) {
            options.add(new PromptOption(reputation.name(), reputation.name(), reputation.getDescription()));
        }
        return options;
    }

    static List<PromptOption> districtOptions() {
        List<PromptOption> options = new ArrayList<>();
        for (aigm.gamestate.campaign.District district : aigm.gamestate.campaign.District.values()) {
            options.add(new PromptOption(district.getName(), district.getName(), district.getDescription()));
        }
        return options;
    }

    static List<PromptOption> abilityOptions(List<Ability> pool) {
        List<PromptOption> options = new ArrayList<>();
        for (Ability ability : pool) {
            String id = ability instanceof Enum<?> e ? e.name() : ability.getName();
            options.add(new PromptOption(id, ability.getName(), ability.getDescription()));
        }
        return options;
    }

    static List<PromptOption> contactOptions(List<Contact> pool) {
        List<PromptOption> options = new ArrayList<>();
        for (Contact contact : pool) {
            String id = contact instanceof Enum<?> e ? e.name() : contact.getName();
            options.add(new PromptOption(id, contact.getName(), contact.getDescription()));
        }
        return options;
    }

    static List<PromptOption> upgradeOptions(List<Upgrade> pool) {
        List<PromptOption> options = new ArrayList<>();
        for (Upgrade upgrade : pool) {
            String id = upgrade instanceof Enum<?> e ? e.name() : upgrade.getName();
            String extra = upgrade.getCost() > 0 ? "Cost " + upgrade.getCost() + ". " : "";
            options.add(new PromptOption(id, upgrade.getName(), extra + upgrade.getDescription()));
        }
        return options;
    }

    static List<PromptOption> playbookAbilityOptions(Playbook playbook) {
        return playbook == null ? List.of() : abilityOptions(playbook.getAvailableAbilities());
    }

    static List<PromptOption> playbookContactOptions(Playbook playbook) {
        return playbook == null ? List.of() : contactOptions(playbook.getAvailableContacts());
    }

    private static PromptOption purveyor(ViceKind kind, String name, String description) {
        return new PromptOption(name, name, description, kind.name());
    }
}
