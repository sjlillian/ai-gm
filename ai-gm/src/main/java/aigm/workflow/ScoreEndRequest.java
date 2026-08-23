package aigm.workflow;

import aigm.activities.Activities;

/** Signaled when the table/GM ends the score and wants payoff + heat resolved. */
public record ScoreEndRequest(
    boolean success,
    boolean atWar,
    int crewTier,
    Activities.HeatContext heatContext
) {
    public static ScoreEndRequest simple(boolean success, int crewTier, int baseHeat) {
        return new ScoreEndRequest(
            success,
            false,
            crewTier,
            new Activities.HeatContext(baseHeat, false, false, false, false, 0)
        );
    }
}
