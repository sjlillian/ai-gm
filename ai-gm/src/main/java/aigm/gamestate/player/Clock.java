package aigm.gamestate.player;

import java.io.Serializable;

import lombok.Data;

@Data
public class Clock implements Serializable {
    private String name;
    private int progress;
    private int max;

    public Clock() {
        this.name = "";
        this.progress = 0;
        this.max = 4; // Default clock size
    }

    public Clock(String name, int max) {
        this.name = name;
        this.progress = 0;
        this.max = max;
    }

    public Clock(String name, int progress, int max) {
        this.name = name;
        this.progress = progress;
        this.max = max;
    }

    public void tick() {
        tick(1);
    }

    public void tick(int amount) {
        this.progress = Math.min(this.progress + amount, this.max);
    }

    public boolean isComplete() {
        return this.progress >= this.max;
    }

    public String toString() {
        return name + ": " + progress + "/" + max;
    }
}
