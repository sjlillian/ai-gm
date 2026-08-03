package aigm.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

import java.util.Map;

import aigm.gamestate.Clock;

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
     * Runs the full score: engagement roll -> plan/detail -> free-form action rolls
     * against clocks (via signals from the table/app) -> payoff determination.
     * Returns a ScoreResult (payoff coin, heat generated, notes) that CampaignWorkflow
     * feeds into the following DowntimeWorkflow.
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

    /** Read-only snapshot of clocks currently active during this score, for UI. */
    @QueryMethod
    Map<String, Clock> getClocks();
}