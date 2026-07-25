package aigm.gamestate.player;

import lombok.Data;

@Data
public class Heat {

    private Clock heat;
    private int wantedLevel;

    public Heat() {
        this.heat = new Clock("heat", 9);
        this.wantedLevel = 0;
    }

    public void updateHeat(int delta) {
        heat.tick(delta);
        if (heat.isComplete()) {
            heat.setProgress(0);
            wantedLevel++;
        }
    }
}
