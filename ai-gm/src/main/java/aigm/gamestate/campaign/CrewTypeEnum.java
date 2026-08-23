package aigm.gamestate.campaign;

import java.util.List;

import aigm.gamestate.Ability;

public enum CrewTypeEnum implements CrewType {
    ASSASSINS,
    BRAVOS,
    CULT,
    HAWKERS,
    SHADOWS,
    SMUGGLERS,
    CUSTOM;

    private final String type;
    private final String xpTrigger;
    private final List<Upgrade> upgrades;
    private final List<Ability> abilities;
    private final List<Claim> claims;

    CrewTypeEnum() {
        this.type = this.name();
        this.xpTrigger = "";
        this.upgrades = List.of();
        this.abilities = List.of();
        this.claims = List.of();
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
    public List<Claim> getClaims() {
        return claims;
    }
}
