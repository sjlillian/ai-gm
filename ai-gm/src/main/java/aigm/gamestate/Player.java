package aigm.gamestate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import aigm.gamestate.enums.Action;

public class Player implements Serializable{

    private String name;
    private int stress;
    //private Truama truama;
    private Map<Action, Integer> actionRatings;
    private Harm harm;
    private int coin;
    private int stash;

    public Player() {
        // default constructor for serialization
    }

    public Player(String name) {
        this.name = name;
        this.stress = 0;
        //this.truama = new Truama();
        // Initialize action ratings to 0
        this.actionRatings = new HashMap<>();
        for (Action action : Action.values()) {
            actionRatings.put(action, 0);
        }
        this.harm = new Harm();
        this.coin = 0;
        this.stash = 0;
    }

    public void updateStress(int delta) {
        this.stress = this.stress + delta;
    }

    public String getName() {
        return name;
    }

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) {
        this.stress = stress;
    }

    public int getActionRating(Action action) {
        return actionRatings.getOrDefault(action, 0);
    }

    public Harm getHarm() {
        return harm;
    }

    public int getCoin() {  
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getStash() {
        return stash;
    }

    public void setStash(int stash) {
        this.stash = stash;
    }

}
