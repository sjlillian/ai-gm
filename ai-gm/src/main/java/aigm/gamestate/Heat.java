package aigm.gamestate;

public class Heat {

    private Clock heat;
    private int wantedLevel;

    public Heat() {
        this.heat = new Clock("heat", 9);
        this.wantedLevel = 0;
    }

    public void increaseHeat(int amount) {
        heat.tick(amount);
        if (heat.isComplete()) {
            heat.setProgress(0);
            wantedLevel++;
        }
    }

    public Clock getHeat() {
        return heat;
    }

    public int getWantedLevel() {
        return wantedLevel;
    }
}
