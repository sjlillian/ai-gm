package aigm.gamestate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public void addMember(Player player) {
        members.add(player);
    }

    public List<Player> getMembers() {
        return members;
    }
}
