package aigm.activities;

import java.util.List;

import aigm.gamestate.DiceRoll;
import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.Heat;
import aigm.gamestate.player.Action;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

/**
 * Non-deterministic work for the campaign: dice, rulebook table lookups that
 * depend on a roll, and LLM GM adjudication/narration.
 * <p>
 * Deterministic sheet mutations (stress, coin, clocks) belong in workflow
 * signal handlers, not here. Player input belongs on workflow signals, not
 * stdin inside a worker.
 */
@ActivityInterface
public interface Activities {

    @ActivityMethod
    DiceRoll rollAction(int actionRating, boolean pushYourself, boolean assisted);

    @ActivityMethod
    DiceRoll rollFortune(int dice);

    @ActivityMethod
    DiceRoll rollResistance(int attributeRating);

    @ActivityMethod
    DiceRoll rollEngagement(int dice);

    @ActivityMethod
    DiceRoll rollVice(int viceRating);

    @ActivityMethod
    PayoffResult determinePayoff(int targetTier, int crewTier, boolean atWar);

    @ActivityMethod
    HeatResult determineHeat(HeatContext context);

    @ActivityMethod
    EntanglementResult rollEntanglement(Heat.WantedLevel wantedLevel, int heat);

    @ActivityMethod
    AcquireAssetResult acquireAsset(int crewTier, String assetDescription);

    @ActivityMethod
    RecoveryRollResult recover(int treatmentQuality);

    @ActivityMethod
    DiceRoll reduceHeat(int dice);

    @ActivityMethod
    Adjudication adjudicateAction(String situation, String approach, Action chosenAction);

    @ActivityMethod
    String narrate(String situation, String mechanicalOutcome);

    record PayoffResult(int coin, int rep, String notes) {}

    record HeatContext(
        int baseHeat,
        boolean highProfile,
        boolean killing,
        boolean hostileTurf,
        boolean wellConnectedTarget,
        int extra
    ) {}

    record HeatResult(int heat, String notes) {}

    /**
     * Entanglement from Core Rulebook pp. 150–152.
     * {@code name}/{@code description} summarize the row; when the table lists
     * two options the GM picks one via {@code options}.
     */
    record EntanglementResult(
        String name,
        String description,
        List<String> options,
        int roll,
        String heatColumn
    ) {
        public EntanglementResult {
            options = options == null ? List.of() : List.copyOf(options);
        }
    }

    record AcquireAssetResult(int quality, String notes) {}

    record RecoveryRollResult(int segments, String notes) {}

    record Adjudication(
        Action action,
        Position position,
        Effect effect,
        String reasoning,
        List<String> possibleStakes
    ) {}
}
