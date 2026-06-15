package aigm.gamestate.score;

public class ScoreOutcome {

    private boolean success;
    private int heatGained;
    private int repGained;
    private int xpGained;
    private int coinsGained;

    public ScoreOutcome() {
        this.success = false;
        this.heatGained = 0;
        this.repGained = 0;
        this.xpGained = 0;
        this.coinsGained = 0;
    }

    public ScoreOutcome(boolean success, int heatGained, int repGained, int xpGained, int coinsGained) {
        this.success = success;
        this.heatGained = heatGained;
        this.repGained = repGained;
        this.xpGained = xpGained;
        this.coinsGained = coinsGained;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getHeatGained() {
        return heatGained;
    }

    public int getRepGained() {
        return repGained;
    }

    public int getXpGained() {
        return xpGained;
    }

    public int getCoinsGained() {
        return coinsGained;
    }

}
