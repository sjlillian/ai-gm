package aigm.llm;

import java.util.List;

import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.RelationshipStatus;
import aigm.gamestate.player.Action;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Temporal activities that call an {@link LlmClient}. Register
 * {@link LlmActivitiesImpl} on the worker next to dice {@code ActivitiesImpl}.
 */
@ActivityInterface
public interface LlmActivities {

    @ActivityMethod
    Adjudication adjudicateAction(String situation, String approach, Action chosenAction);

    @ActivityMethod
    String narrate(String situation, String mechanicalOutcome);

    @ActivityMethod
    StartingSituation generateStartingSituation(String crewSummary);

    record Adjudication(
        Action action,
        Position position,
        Effect effect,
        String reasoning,
        List<String> possibleStakes
    ) {
        public Adjudication {
            possibleStakes = possibleStakes == null ? List.of() : List.copyOf(possibleStakes);
            reasoning = reasoning == null ? "" : reasoning;
        }
    }

    record ClockSpec(String name, int segments) {}

    record FactionNote(String faction, RelationshipStatus status) {}

    record ScoreSeed(
        String title,
        String hook,
        String targetName,
        String targetTier,
        String planType,
        String district
    ) {
        public ScoreSeed {
            title = title == null ? "" : title;
            hook = hook == null ? "" : hook;
            targetName = targetName == null ? "" : targetName;
            targetTier = targetTier == null ? "" : targetTier;
            planType = planType == null ? "" : planType;
            district = district == null ? "" : district;
        }
    }

    record StartingSituation(
        String fiction,
        List<ClockSpec> clocks,
        List<FactionNote> factions,
        List<ScoreSeed> scores
    ) {
        public StartingSituation {
            fiction = fiction == null ? "" : fiction;
            clocks = clocks == null ? List.of() : List.copyOf(clocks);
            factions = factions == null ? List.of() : List.copyOf(factions);
            scores = scores == null ? List.of() : List.copyOf(scores);
        }
    }
}
