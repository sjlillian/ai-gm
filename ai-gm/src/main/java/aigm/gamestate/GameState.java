package aigm.gamestate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import aigm.gamestate.enums.Phase;

public record GameState(
        Phase phase,
        List<Crew> crews,
        List<Player> players,
        List<Clock> clocks
) implements Serializable {

    public GameState() {
        this(Phase.FREEPLAY, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public GameState changeStress(String player, int stressDelta) {
        Player updatedPlayer = this.players.stream().filter(
            p -> p.getName().equals(player)).findFirst().orElse(new Player(player));

        updatedPlayer.updateStress(stressDelta);

        List<Player> updatedPlayerStress = new ArrayList<Player>();

        for(Player oldPlayer: this.players) {
            if (player.equals(updatedPlayer)) {
                updatedPlayerStress.add(updatedPlayer);
                break;
            }
            updatedPlayerStress.add(oldPlayer);
        }

        return new GameState(
            this.phase,
            this.crews,
            updatedPlayerStress,
            this.clocks
        );
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Phase: ").append(phase).append("\n");
        sb.append("Players:\n");
        for (Player player : players) {
            sb.append("- ").append(player.getName()).append(": Stress=").append(player.getStress()).append("\n");
        }
        sb.append("Clocks:\n");
        for (Clock clock : clocks) {
            sb.append("- ").append(clock.getName()).append(": ").append(clock.getProgress()).append("/").append(clock.getMax()).append("\n");
        }
        return sb.toString();
    }
}