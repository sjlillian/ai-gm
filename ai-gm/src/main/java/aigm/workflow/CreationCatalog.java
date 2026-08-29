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
}
