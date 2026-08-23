package aigm.gamestate;

public record Clock(
    String name,
    int progress,
    int max
) {

    public Clock(String name, int max) {
        this(name, 0, max);
    }

    public Clock {
        if (max < 1) {
            throw new IllegalArgumentException("Clock max must be at least 1");
        }
        progress = Math.max(0, Math.min(progress, max));
    }

    public Clock tick(int delta) {
        return new Clock(name, progress + delta, max);
    }

    /**
     * Apply {@code delta}, filling the clock as many times as needed.
     * Used for heat (wanted level) and similar overflow tracks.
     */
    public Overflow tickOverflowing(int delta) {
        int total = progress + delta;
        if (total < 0) {
            return new Overflow(new Clock(name, 0, max), 0);
        }
        int completions = total / max;
        int remainder = total % max;
        return new Overflow(new Clock(name, remainder, max), completions);
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
        return new Clock(name, Math.min(progress, max), max);
    }

    public record Overflow(Clock clock, int completions) {}
}
