package aigm.gamestate.dto;

import java.util.List;

public class CrewType {

    private String type;
    private String xpTrigger;
    private List<Upgrade> upgrades;
    private List<Ability> abilities;
    private List<Claim> claims;

    public CrewType() {
        // default constructor for serialization
    }

    public CrewType(String type, String xpTrigger, List<Upgrade> upgrades, List<Ability> abilities, List<Claim> claims) {
        this.type = type;
        this.xpTrigger = xpTrigger;
        this.upgrades = upgrades;
        this.abilities = abilities;
        this.claims = claims;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXpTrigger() {
        return xpTrigger;
    }

    public void setXpTrigger(String xpTrigger) {
        this.xpTrigger = xpTrigger;
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(List<Upgrade> upgrades) {
        this.upgrades = upgrades;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public void addUpgrade(Upgrade upgrade) {
        this.upgrades.add(upgrade);
    }

    public void addAbility(Ability ability) {
        this.abilities.add(ability);
    }

    public void addClaim(Claim claim) {
        this.claims.add(claim);
    }

}
