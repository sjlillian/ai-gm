package aigm.gamestate.campaign;

import java.util.List;

import aigm.gamestate.Ability;

public interface CrewType {

    String getType();
    String getXPTrigger();
    List<Upgrade> getUpgrades();
    List<Ability> getAbilities();
    List<Claim> getClaims();    

}
