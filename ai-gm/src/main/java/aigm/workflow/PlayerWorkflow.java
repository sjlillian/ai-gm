package aigm.workflow;

import aigm.gamestate.player.Player;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Long-running workflow representing a single Player Character. Started as a child of
 * CampaignWorkflow, one per PC, at campaign creation time.
 * <p>
 * Lifecycle notes:
 * - Workflow ID should be deterministic, e.g. "pc-{crewId}-{pcId}", so ScoreWorkflow /
 *   DowntimeWorkflow can construct a workflow stub and signal it directly without
 *   needing a handle passed down every time.
 * - Like CampaignWorkflow, this is effectively a permanent entity for the life of the
 *   character. Structure run() as: Workflow.await() on incoming signals, apply them to
 *   PCState, loop. Call Workflow.continueAsNew(currentState) periodically (e.g. after
 *   every score+downtime cycle, or every N signals) to keep history bounded — a PC could
 *   accumulate stress/harm/xp signals across a very long campaign.
 * - Consider a "retire"/"kill"/"replace character" signal that lets the workflow
 *   actually complete (return) rather than continue indefinitely, for when a PC dies,
 *   retires, or a player leaves — otherwise this runs forever.
 * <p>
 * State ownership:
 * - This workflow is the ONLY thing that mutates PCState (stress, harm, trauma, xp
 *   tracks, vice, load-out, personal clocks/long-term projects, advancement).
 * - ScoreWorkflow and DowntimeWorkflow never touch PCState directly — they call these
 *   signal methods with the outcome of an Activity call (e.g. DowntimeActivities
 *   .indulgeVice(...) returns a result, which ScoreWorkflow/DowntimeWorkflow then
 *   forwards here via resolveVice(...)).
 */
@WorkflowInterface
public interface PlayerWorkflow {

    @WorkflowMethod
    void run(Player player);

    /** Signaled during a score when the PC marks stress (resisting a consequence, etc.). */
    @SignalMethod
    void markStress();

    /** Signaled during a score, or by an entanglement resolution, when the PC takes harm. */
    @SignalMethod
    void takeHarm();

    /**
     * Signaled at end of session / during play when XP is marked on a track.
     * Should check advancement thresholds and may need to expose a "ready to advance"
     * flag via query, or trigger a separate advancement signal/prompt back to the app.
     */
    @SignalMethod
    void markXp();

    /**
     * Signaled by DowntimeWorkflow with the result of DowntimeActivities.indulgeVice(...).
     * Applies stress clearing and, if overindulgence occurred, records the consequence
     * (may itself add an entanglement or trauma — coordinate with CampaignWorkflow via
     * a further signal if the overindulgence outcome affects crew-level state).
     */
    @SignalMethod
    void resolveVice();

    /**
     * Signaled by DowntimeWorkflow with the result of DowntimeActivities.recoverHarm(...).
     * Applies segments to the PC's healing clock; when full, reduces harm per the rules
     * and rolls over remaining segments.
     */
    @SignalMethod
    void applyRecovery();

    /**
     * Signaled by DowntimeWorkflow with the result of DowntimeActivities
     * .workOnLongTermProject(...). Applies segments to the named personal clock.
     */
    @SignalMethod
    void applyProjectProgress();

    /** Signaled when a new personal long-term project clock is started. */
    @SignalMethod
    void startProject();

    /** Signaled by DowntimeWorkflow with the result of DowntimeActivities.acquireAsset(...)
     *  when the PC (not the crew) is the one acquiring the asset. */
    @SignalMethod
    void addPersonalAsset();

    /** Read-only snapshot for UI/GM tooling. */
    @QueryMethod
    Player getState();
}
