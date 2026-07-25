package aigm.gamestate.score;

import lombok.Data;

@Data
public class Score {

    private String title;
    private ScoreType type;
    private ScoreStatus status;
    private ScoreRuntime runtime;
    private ScoreOutcome outcome;

    public Score() {
        this.type = ScoreType.ASSAULT;
        this.status = ScoreStatus.IN_PROGRESS;
        this.runtime = new ScoreRuntime();
        this.outcome = new ScoreOutcome();
    }

    public Score(String title, ScoreType type) {
        this.title = title;
        this.type = type;
        this.status = ScoreStatus.IN_PROGRESS;
        this.runtime = new ScoreRuntime();
        this.outcome = new ScoreOutcome();
    }
}
