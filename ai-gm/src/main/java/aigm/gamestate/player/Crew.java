package aigm.gamestate.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import aigm.gamestate.dto.Ability;
import aigm.gamestate.dto.Contact;
import aigm.gamestate.dto.CrewType;
import aigm.gamestate.dto.Upgrade;
import aigm.gamestate.score.Score;
import lombok.Data;

@Data
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

    public void addMember(Player player) {
        members.add(player);
    }

    public void updateHeat(int delta) {
        heat.updateHeat(delta);
    }

    public void addTurf(int amount) {
        crewStanding.addTurf(amount);
    }

    public void addRep(int amount) {
        crewStanding.addRep(amount);
    }

    public void advanceHold() {
        crewStanding.advanceHold();
    }

    public void advanceTier() {
        crewStanding.advanceTier();
    }

    public void reduceHold() {
        crewStanding.reduceHold();
    }

    public void reduceTier() {
        crewStanding.reduceTier();
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

    public void addClock(Clock newClock) {
        clocks.add(newClock);
    }
}
