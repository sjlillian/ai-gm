package aigm.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.UpdateMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.Map;

import aigm.gamestate.Clock;
import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.player.Action;
import aigm.llm.gm.LlmActivities;

/**
 * Short-lived (relative to Campaign/PC) workflow representing a single score/heist.
 * Started as a child workflow of CampaignWorkflow.
 * <p>
 * Lifecycle notes:
 * - Workflow ID e.g. "score-{crewId}-{scoreNumber}".
 * - This workflow does NOT need continueAsNew — a single score is bounded in duration
 *   and event volume (unlike Campaign/PC workflows). It should run() to completion and
 *   return a ScoreResult, at which point CampaignWorkflow starts the next child
 *   (DowntimeWorkflow) using that result.
 * - Holds only transient state: the in-score progress clocks (obstacles, "Alert" clocks,
 *   linked clocks, etc.) and the log of action rolls. None of this outlives the score,
 *   so it does not need to be persisted into CrewState/PCState — only the final
 *   consequences do (heat, coin, harm, stress, entanglement seed).
 * <p>
 * State ownership / signal-forwarding:
 * - This workflow does NOT mutate CrewState or PCState directly. Every consequence
 *   during the score (stress marked, harm taken, heat generated) should be forwarded
 *   via a signal to the relevant PlayerCharacterWorkflow or CampaignWorkflow as it
 *   happens (or batched at score end — see note on recordActionRoll below).
 * - The Engagement Roll and Entanglement precursor lookups are good candidates for
 *   Activities (ScoreActivities) since they hit fixed tables and you may want them
 *   logged/audited.
 */
@WorkflowInterface
public interface ScoreWorkflow {

    /**
     * Runs the score: plan type + one detail, then engagement roll (sets starting
     * position), then free-form action rolls against clocks, then payoff and heat.
     * Entanglement is rolled in DowntimeWorkflow after heat is applied.
     * Returns a ScoreResult that CampaignWorkflow feeds into the following downtime.
     */
    @WorkflowMethod
    ScoreResult run(ScoreRequest request);

    /**
     * Signaled by the GM/app to start or update a named clock (obstacle, alert level,
     * linked clock, etc.). Mutates this workflow's own in-memory clock map only.
     */
    @SignalMethod
    void tickClock(String clockId, int segments);

    /**
     * Signaled when an action roll is resolved at the table. Depending on design, this
     * can either:
     *   (a) immediately forward consequences to PlayerCharacterWorkflow/CampaignWorkflow
     *       via their signal methods as they happen, or
     *   (b) accumulate into an in-memory log and batch-apply at the end of run().
     * Prefer (a) for stress/harm (so PC state is live/accurate mid-score for anyone
     * watching), and reserve batching only for things that must be computed once at
     * score end (e.g. total payoff).
     */
    @SignalMethod
    void recordActionRoll(ActionRollResult result);

    /**
     * Signaled by the GM/app when the score is over. Unblocks run() to resolve
     * payoff and heat, forward them to CampaignWorkflow, and return.
     */
    @SignalMethod
    void endScore(ScoreEndRequest end);

    /**
     * GM call: given fiction + the action the player wants, the LLM sets
     * position/effect (no dice). Call this before {@link #recordActionRoll}.
     */
    @UpdateMethod
    LlmActivities.Adjudication adjudicate(String situation, String approach, Action chosenAction);

    /**
     * Rolls the action dice in an activity, records the result on this score, and returns it.
     * Call after {@link #adjudicate} once position/effect are set.
     */
    @UpdateMethod
    ActionRollResult resolveAction(
        String pcId,
        Action action,
        int actionRating,
        Position position,
        Effect effect,
        boolean push,
        boolean assist,
        String consequence
    );

    /** Read-only snapshot of clocks currently active during this score, for UI. */
    @QueryMethod
    Map<String, Clock> getClocks();

    @QueryMethod
    Position getEngagementPosition();

    @QueryMethod
    LlmActivities.Adjudication getLastAdjudication();
}