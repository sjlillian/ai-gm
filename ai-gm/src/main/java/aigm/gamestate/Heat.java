package aigm.gamestate;

public class Heat {

    private Clock heat;
    private int wantedLevel;

    public Heat() {
        this.heat = new Clock(9);
        this.wantedLevel = 0;
    }

    public void increaseHeat(int amount) {
        heat.addProgress(amount);
        if (heat.isComplete()) {
            heat.reset();
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
