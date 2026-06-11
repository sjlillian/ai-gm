package aigm.gamestate;

import java.io.Serializable;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int value) {
        this.max = value;
    }

}
