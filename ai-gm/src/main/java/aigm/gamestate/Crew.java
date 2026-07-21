package aigm.gamestate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import aigm.gamestate.dto.Ability;
import aigm.gamestate.dto.Contact;
import aigm.gamestate.dto.CrewType;
import aigm.gamestate.dto.Upgrade;
import aigm.gamestate.score.Score;

public class Crew implements Serializable{

    private String name;
    private CrewType type;
    private List<Player> members;
    private Heat heat;
    private CrewStanding crewStanding;
    private Clock crewXP;
    private List<Ability> abilities;
    private List<Upgrade> upgrades;
    private List<Contact> contacts;
    private List<Score> scores;
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
        this.abilities = new ArrayList<>();
        this.upgrades = new ArrayList<>();
        this.contacts = new ArrayList<>();
        this.scores = new ArrayList<>();
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

    public List<Contact> getContacts() {
        return contacts;
    }

    public List<Score> getScores() {
        return scores;
    }
    public List<Ability> getAbilities() {
        return abilities;
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }
    public void addUpgrade(Upgrade upgrade) {
        upgrades.add(upgrade);
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public void addScore(Score score) {
        scores.add(score);
    }
}
