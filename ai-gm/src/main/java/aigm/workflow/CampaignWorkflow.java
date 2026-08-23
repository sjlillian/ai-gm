package aigm.workflow;

import aigm.gamestate.Clock;
import aigm.gamestate.campaign.Claim;
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
 *   with ParentClosePolicy.ABANDON so they survive CampaignWorkflow.continueAsNew.
 *   Persist their workflow IDs so ScoreWorkflow / DowntimeWorkflow can signal them
 *   as external workflows.
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
    void run(CampaignState state);

    /**
     * Signaled by the GM/app when the crew is ready to start a score (engagement roll
     * about to happen). Should set a flag/queue that the run() loop's Workflow.await()
     * is waiting on, carrying the ScoreRequest to pass into the child ScoreWorkflow.
     */
    @SignalMethod
    void startScore(ScoreRequest request);

    /**
     * Signaled by ScoreWorkflow or DowntimeWorkflow (not called directly by external
     * clients) when crew heat changes. Mutates crew heat; filling the track raises
     * wanted level and clears heat.
     */
    @SignalMethod
    void adjustHeat(int delta);

    /**
     * Signaled by ScoreWorkflow (payoff) or DowntimeWorkflow (acquire asset costs,
     * extra downtime activity costs) when crew coin changes. Mutates Crew.coin.
     */
    @SignalMethod
    void adjustCoin(int delta);

    /**
     * Signaled when crew reputation changes (score payoff, entanglement costs, etc.).
     * Filling the rep track should call Crew.tryAdvance() here, since this workflow owns Tier.
     */
    @SignalMethod
    void adjustRep(int delta);

    /**
     * Signaled when a claim is added (turf war results, entanglement outcomes
     * like "Show of Force"). Turf claims should also resize the rep track.
     */
    @SignalMethod
    void addClaim(Claim claim);

    /**
     * Signaled by DowntimeWorkflow when a crew-level asset is acquired (as opposed to
     * a PC's personal acquire-asset roll, which instead affects PlayerWorkflow).
     */
    @SignalMethod
    void addCrewAsset(String asset);

    /** Permanently archive the campaign; run() returns instead of continueAsNew. */
    @SignalMethod
    void endCampaign();

    /** Read-only snapshot of current crew state, for UI/GM tooling. */
    @QueryMethod
    Crew getCrew();

    /** Read-only snapshot of crew-level (non-PC) progress clocks currently active. */
    @QueryMethod
    java.util.List<Clock> getActiveClocks();

    @QueryMethod
    Phase getPhase();
}
