package aigm.gamestate.score;

import aigm.gamestate.campaign.Entanglement;
import lombok.Data;

@Data
public class ScoreOutcome {

    private int targetTier;
    private int coinGained;
    private int repGained;
    private int heatGained;
    private Entanglement entanglement;
    private String notes;

    public ScoreOutcome() {
        this.targetTier = 0;
        this.coinGained = 0;
        this.repGained = 0;
        this.heatGained = 0;
        this.entanglement = null;
        this.notes = "";
    }

    public ScoreOutcome(int targetTier, int coinGained, int repGained, int heatGained, Entanglement entanglement, String notes) {
        this.targetTier = targetTier;
        this.coinGained = coinGained;
        this.repGained = repGained;
        this.heatGained = heatGained;
        this.entanglement = entanglement;
        this.notes = notes;
    }
}
