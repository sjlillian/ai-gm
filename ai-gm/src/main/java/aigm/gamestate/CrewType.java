package aigm.gamestate;

import java.util.List;

import aigm.gamestate.dto.Ability;
import aigm.gamestate.dto.Claim;
import aigm.gamestate.dto.Upgrade;

public class CrewType {

    private String type;
    private String xpTrigger;
    private List<Upgrade> upgrades;
    private List<Ability> abilities;
    private List<Claim> claims;

}
