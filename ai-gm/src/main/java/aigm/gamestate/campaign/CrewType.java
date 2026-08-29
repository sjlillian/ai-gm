package aigm.gamestate.campaign;

import java.util.List;

import aigm.gamestate.Ability;
import aigm.gamestate.Contact;

public interface CrewType {

    String getType();
    String getXPTrigger();
    List<Upgrade> getUpgrades();
    List<Ability> getAbilities();
    List<Contact> getContacts();
    List<Claim> getClaims();

    /** Printed starting upgrades for this type. Custom types default to none. */
    default List<Upgrade> getStartingUpgrades() {
        return List.of();
    }

}
