package aigm.workflow;

import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.score.ScoreType;

/** A GM-offered job the crew can investigate, then take as a score. */
public record ScoreOpportunity(
    String id,
    String title,
    String hook,
    String targetName,
    CrewStanding.Tier targetTier,
    ScoreType planType,
    String district
) {
    public ScoreOpportunity {
        id = id == null ? "" : id;
        title = title == null ? "" : title;
        hook = hook == null ? "" : hook;
        targetName = targetName == null ? "" : targetName;
        targetTier = targetTier == null ? CrewStanding.Tier.ZERO : targetTier;
        planType = planType == null ? ScoreType.STEALTH : planType;
        district = district == null ? "" : district;
    }
}
