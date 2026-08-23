package aigm.gamestate.score;

import lombok.Data;

@Data
public class ScoreOutcome {

    private int targetTier;
    private int coinGained;
    private int repGained;
    private int heatGained;
    private String entanglement;
    private String notes;

    public ScoreOutcome() {
        this.targetTier = 0;
        this.coinGained = 0;
        this.repGained = 0;
        this.heatGained = 0;
        this.entanglement = "";
        this.notes = "";
    }

    public ScoreOutcome(int targetTier, int coinGained, int repGained, int heatGained, String entanglement, String notes) {
        this.targetTier = targetTier;
        this.coinGained = coinGained;
        this.repGained = repGained;
        this.heatGained = heatGained;
        this.entanglement = entanglement;
        this.notes = notes;
    }
}
