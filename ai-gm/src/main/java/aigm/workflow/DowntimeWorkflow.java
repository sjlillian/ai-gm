package aigm.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.UpdateMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * One downtime phase, shared by the whole crew (not per-PC — the crew-level
 * entanglement roll and the "2 free activities per PC" pacing both apply to the
 * phase as a whole). Started as a child workflow of CampaignWorkflow after a
 * ScoreWorkflow completes.
 * <p>
 * Lifecycle notes:
 * - Workflow ID e.g. "downtime-{crewId}-{cycleNumber}".
 * - No continueAsNew needed — a single downtime phase is bounded (crew size * a small
 *   number of activities each) and should run() to completion and return a
 *   DowntimeResult.
 * - This workflow will likely spend most of its life in Workflow.await(), waiting for
 *   each PC's player to submit their activity choice(s) via chooseActivity(...) —
 *   this could span real hours/days between sessions if played async, which is fine;
 *   Temporal workflows are durable and cheap to leave waiting.
 * - If your group plays fully async (play-by-post) and a single PC's activity choice
 *   might itself need a long sub-conversation (e.g. GM and player going back and forth
 *   on a long-term project's direction), consider promoting "one PC's downtime turn"
 *   to its own child workflow instead of handling it as a signal branch here. For a
 *   live-at-the-table game, keep it simple as one shared workflow.
 * <p>
 * State ownership / signal-forwarding:
 * - This workflow does NOT mutate CrewState or PCState directly.
 * - The entanglement roll (Activity) result is crew-wide; depending on the specific
 *   entanglement, forward its consequences to CampaignWorkflow (heat/rep/coin/claim
 *   changes) and/or to a specific PlayerCharacterWorkflow (e.g. "Interrogation" harm).
 * - Each per-PC activity result gets forwarded to that PC's PlayerCharacterWorkflow
 *   (train, recover, indulge vice, personal acquire asset, personal project progress)
 *   or to CampaignWorkflow (crew acquire asset, reduce heat).
 */
@WorkflowInterface
public interface DowntimeWorkflow {

    /**
     * Runs the full downtime phase:
     *   1. Call Activities.rollEntanglement(...) once for the crew, apply/narrate
     *      the result (after score payoff/heat are already on the crew).
     *   2. Workflow.await() for each PC to submit 1-2 (or more, at coin/rep cost)
     *      activity choices via chooseActivity(...).
     *   3. For each submitted choice, call the matching Activities method,
     *      then forward the result via signal to the owning workflow (PC or Crew).
     *   4. Return once all PCs have either submitted or the GM force-closes the phase
     *      (see closeDowntime()).
     */
    @WorkflowMethod
    DowntimeResult run(DowntimeRequest request);

    /**
     * Signaled once per activity a PC takes (they may call this twice for their two
     * free activities, or more if paying coin/rep for extras). Should be validated
     * against remaining free/paid activity slots for that PC (tracked in this
     * workflow's own transient state, not PCState).
     */
    @SignalMethod
    void chooseActivity(String pcId, DowntimeActivityChoice choice);

    /**
     * Same as {@link #chooseActivity} but rejects unknown PCs and over-limit unpaid
     * extras so the UI can show the error.
     */
    @UpdateMethod
    void submitActivity(String pcId, DowntimeActivityChoice choice);

    @QueryMethod
    String getEntanglement();

    /**
     * Signaled by the GM/app once every PC has finished choosing, to unblock the
     * final Workflow.await() in run() and let the workflow return. Alternatively,
     * run() can auto-detect completion once every PC has used all their slots —
     * this signal is a manual override for "we're done, move on" even if someone
     * didn't use both activities.
     */
    @SignalMethod
    void closeDowntime();

    /** Read-only snapshot of activity choices submitted so far, for UI. */
    @QueryMethod
    java.util.Map<String, java.util.List<DowntimeActivityChoice>> getSubmittedActivities();
}