package aigm.client;

import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.score.ScoreType;

/** Input to begin a score from any UI. */
public record StartScoreCommand(
    String scoreId,
    String title,
    ScoreType planType,
    String planDetail,
    String targetName,
    CrewStanding.Tier targetTier,
    int engagementDice
) {}
