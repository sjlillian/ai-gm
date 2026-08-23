package aigm.workflow;

import aigm.activities.Activities;

/** Signaled when the table/GM ends the score and wants payoff + heat resolved. */
public record ScoreEndRequest(
    boolean success,
    boolean atWar,
    int crewTier,
    Activities.HeatContext heatContext
) {}
