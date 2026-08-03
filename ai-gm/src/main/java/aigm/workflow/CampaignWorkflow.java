package aigm.workflow;

import aigm.gamestate.Clock;
import aigm.gamestate.campaign.Crew;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Root, long-running workflow for a single Crew's campaign.
 * <p>
 * Lifecycle notes:
 * - One instance per Crew, workflow ID should be deterministic, e.g. "campaign-{crewId}".
 * - This workflow effectively never "completes" in the normal sense — it lives for the
 *   life of the campaign. The run() loop should be structured as:
 *      1. Workflow.await() until a score is requested (signal) or downtime is requested.
 *      2. Start a ScoreWorkflow as a child, wait for it to finish.
 *      3. Start a DowntimeWorkflow as a child, wait for it to finish.
 *      4. Merge any crew-level results already applied via signals (see below) — most
 *         crew state changes should already be applied incrementally via signals from
 *         the child workflows, not batch-applied at the end.
 *      5. Call Workflow.continueAsNew(...) with the current CrewState to reset history.
 *         Do this after every score+downtime cycle (or sooner, if history grows large
 *         from a chatty score). This workflow is a prime continueAsNew candidate since
 *         it may run for years of real time across a whole campaign.
 * <p>
 * State ownership:
 * - This workflow is the ONLY thing that mutates CrewState (Tier, coin, rep, heat,
 *   wanted level, hold, claims, crew upgrades). ScoreWorkflow and DowntimeWorkflow
 *   do not touch crew state directly — they call the signal methods below.
 * - PlayerWorkflow children are started here (once, at campaign creation)
 *   and their workflow IDs/handles should be persisted in CrewState so ScoreWorkflow /
 *   DowntimeWorkflow can be given the PC workflow IDs to signal directly, without
 *   needing to route everything back through this workflow.
 */
@WorkflowInterface
public interface CampaignWorkflow {

    public enum Phase {
        SCORE,
        DOWNTIME,
        FREEPLAY
    }

    /**
     * Main entry point. Should contain the phase loop described above.
     * Must end each cycle with Workflow.continueAsNew(currentState) rather than returning,
     * except in the rare case the campaign is being permanently archived/ended.
     */
    @WorkflowMethod
    void run(Crew crew);

    /**
     * Signaled by the GM/app when the crew is ready to start a score (engagement roll
     * about to happen). Should set a flag/queue that the run() loop's Workflow.await()
     * is waiting on, carrying the ScoreRequest to pass into the child ScoreWorkflow.
     */
    @SignalMethod
    void startScore();

    /**
     * Signaled by ScoreWorkflow or DowntimeWorkflow (not called directly by external
     * clients) when crew heat changes. Mutates CrewState.heat. Clamp at 0 minimum.
     */
    @SignalMethod
    void adjustHeat();

    /**
     * Signaled by ScoreWorkflow (payoff) or DowntimeWorkflow (acquire asset costs,
     * extra downtime activity costs) when crew coin changes. Mutates CrewState.coin.
     */
    @SignalMethod
    void adjustCoin();

    /**
     * Signaled when crew reputation changes (score payoff, entanglement costs, etc.).
     * Mutates CrewState.rep. Note: crew Tier advancement threshold checks (rep -> Tier)
     * likely belong here too, since this workflow owns Tier.
     */
    @SignalMethod
    void adjustRep();

    /**
     * Signaled when the crew's wanted level changes (independent of heat — heat resets
     * to 0 and wanted level increases when heat maxes out; that rule can live here).
     */
    @SignalMethod
    void adjustWantedLevel();

    /**
     * Signaled when a claim is added/removed (turf war results, entanglement outcomes
     * like "Show of Force", crew upgrades).
     */
    @SignalMethod
    void addClaim();

    /**
     * Signaled by DowntimeWorkflow when a crew-level asset is acquired (as opposed to
     * a PC's personal acquire-asset roll, which instead affects PlayerCharacterWorkflow).
     */
    @SignalMethod
    void addCrewAsset();

    /** Read-only snapshot of current crew state, for UI/GM tooling. */
    @QueryMethod
    Crew getCrew();

    /** Read-only snapshot of crew-level (non-PC) progress clocks currently active. */
    @QueryMethod
    java.util.List<Clock> getActiveClocks();
}
