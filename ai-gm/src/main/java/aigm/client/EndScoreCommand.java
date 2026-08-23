package aigm.client;

import aigm.activities.Activities;

/** Input to close a score and resolve payoff/heat. */
public record EndScoreCommand(
    boolean success,
    boolean atWar,
    int crewTier,
    Activities.HeatContext heatContext
) {
    public static EndScoreCommand simple(boolean success, int crewTier, int baseHeat) {
        return new EndScoreCommand(
            success,
            false,
            crewTier,
            new Activities.HeatContext(baseHeat, false, false, false, false, 0)
        );
    }
}
