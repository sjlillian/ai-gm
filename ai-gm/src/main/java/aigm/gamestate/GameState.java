package aigm.gamestate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GameState implements Serializable {

    private Phase phase; // "freeplay", "score", "downtime"

    private Map<String, Integer> playerStress;
    private Map<String, Integer> clocks;

    public GameState() {
        this.phase = Phase.FREEPLAY;
        this.playerStress = new HashMap<>();
        this.clocks = new HashMap<>();
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public Map<String, Integer> getPlayerStress() {
        return playerStress;
    }

    public void setStress(String player, int stress) {
        this.playerStress.put(player, stress);
    }

    public int getStress(String player) {
        return this.playerStress.getOrDefault(player, 0);
    }

    public Map<String, Integer> getClocks() {
        return clocks;
    }

    public void setClock(String name, int value) {
        this.clocks.put(name, value);
    }

    public int getClock(String name) {
        return this.clocks.getOrDefault(name, 0);
    }
}