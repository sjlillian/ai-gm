package aigm.gamestate;

public record Clock(
    String name,
    int progress,
    int max
) {

    public Clock(String name, int max) {
        this(name, 0, max);
    }

    public Clock tick(int delta) {
        int newProgress = progress + delta;
        if (newProgress > max) {
            newProgress = max;
        } else if (newProgress < 0) {
            newProgress = 0;
        }
        return new Clock(name, newProgress, max);
    }

    public boolean isComplete() {
        return progress >= max;
    }

    
    public Clock withName(String name) {
        return new Clock(name, progress, max);
    }

    public Clock withProgress(int progress) {
        return new Clock(name, progress, max);
    }

    public Clock withMax(int max) {
        return new Clock(name, progress, max);
    }
    
}
