package aigm.gamestate.campaign;

import java.util.List;

import aigm.gamestate.Ability;

public enum CrewTypeEnum implements CrewType {
    CUSTOM,
    HAWK,
    LANCER,
    RAVEN,
    SHADOW,
    WRAITH;

    private String type;
    private String xpTrigger;
    private List<Upgrade> upgrades;
    private List<Ability> abilities;
    private List<Claim> claims;

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
