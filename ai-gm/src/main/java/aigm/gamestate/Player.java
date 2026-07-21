package aigm.gamestate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import aigm.gamestate.dto.Playbook;
import aigm.gamestate.enums.Action;

public class Player implements Serializable{

    private String name;
    private Truama truama;
    private Map<Action, Integer> actionRatings;
    private Harm harm;
    private int coin;
    private int stash;
    private Playbook playbook;

    public Player() {
        // default constructor for serialization
    }

    public Player(String name) {
        this.name = name;
        this.truama = new Truama();
        // Initialize action ratings to 0
        this.actionRatings = new HashMap<>();
        for (Action action : Action.values()) {
            actionRatings.put(action, 0);
        }
        this.harm = new Harm();
        this.coin = 0;
        this.stash = 0;
    } 

    public Player(String name, Playbook playbook) {
        this.name = name;
        this.truama = new Truama();
        // Initialize action ratings based on playbook starting actions
        this.actionRatings = playbook.getStartingActions().stream()
                .collect(HashMap::new, (map, action) -> map.put(action, 1), HashMap::putAll);
        this.harm = new Harm();
        this.coin = 0;
        this.stash = 0;
        this.playbook = playbook;
    }

    public void updateStress(int delta) {
        this.truama.addStress(delta);
    }

    public String getName() {
        return name;
    }

    public Clock getStress() {
        return truama.getStress();
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

    public Playbook getPlaybook() {
        return playbook;
    }

    public void setPlaybook(Playbook playbook) {
        this.playbook = playbook;
    }

}
