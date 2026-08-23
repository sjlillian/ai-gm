package aigm.gamestate.campaign;

import java.util.List;

import aigm.gamestate.Ability;
import aigm.gamestate.Contact;

public record CrewTypeCustom(
    String type,
    String xpTrigger,
    List<Upgrade> upgrades,
    List<Ability> abilities,
    List<Contact> contacts,
    List<Claim> claims
) implements CrewType {

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
}
