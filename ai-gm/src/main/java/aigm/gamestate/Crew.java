package aigm.gamestate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import aigm.gamestate.dto.CrewType;

public class Crew implements Serializable{

    private String name;
    private CrewType type;
    private List<Player> members;
    private Heat heat;
    private CrewStanding crewStanding;
    private Clock crewXP;
    //private List<CrewAbility> abilities;
    //private List<Upgrade> upgrades;
    //private List<Contact> contacts;

    //private List<LairFeature> lair;
    //private List<FactionStatus> factionStatuses;
    //private List<Score> scores;

    private List<Clock> clocks;

    public Crew() {
        // default constructor for serialization
    }

    public Crew(String name, CrewType type) {
        this.name = name;
        this.type = type;
        this.members = new ArrayList<>();
        this.heat = new Heat();
        this.crewStanding = new CrewStanding();
        this.crewXP = new Clock("crewXP", 10);
        this.clocks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CrewType getType() {
        return type;
    }

    public void setType(CrewType type) {
        this.type = type;
    }

    public void addMember(Player player) {
        members.add(player);
    }

    public List<Player> getMembers() {
        return members;
    }

    public Heat getHeat() {
        return heat;
    }

    public CrewStanding getCrewStanding() {
        return crewStanding;
    }

    public Clock getCrewXP() {
        return crewXP;
    }

    public List<Clock> getClocks() {
        return clocks;
    }

    
}
