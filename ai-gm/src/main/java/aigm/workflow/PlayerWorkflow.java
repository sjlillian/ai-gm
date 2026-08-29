package aigm.workflow;

import aigm.gamestate.DiceRoll;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Advancement;
import aigm.gamestate.player.Background;
import aigm.gamestate.player.Harm;
import aigm.gamestate.player.Heritage;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import aigm.gamestate.player.ViceKind;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.UpdateMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Long-running workflow representing a single Player Character. Started as a child of
 * CampaignWorkflow, one per PC, at campaign creation time (or during Session 0 via
 * {@code joinPlayer}). Incomplete sheets run {@code characterCreation()} first.
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

    /** Signaled during a score when the PC marks stress (push, assist, resistance, flashback). */
    @SignalMethod
    void markStress(int amount);

    /** Player chooses the trauma condition after stress overflow. Four trauma conditions retire the PC. */
    @SignalMethod
    void markTrauma(Trauma.Condition condition);

    /** Signaled during a score, or by an entanglement resolution, when the PC takes harm. */
    @SignalMethod
    void takeHarm(String description, Harm.HarmLevel level, boolean armorMarked);

    /**
     * Signaled at end of session / during play when XP is marked on a track.
     * Attribute XP is 6 boxes; playbook XP is 8.
     */
    @SignalMethod
    void markXp(Advancement.XpTrack track, int amount);

    /**
     * Signaled by DowntimeWorkflow with the vice fortune roll. Clears that much stress;
     * overindulgence if the roll exceeds current stress.
     */
    @SignalMethod
    void resolveVice(DiceRoll viceRoll);

    /**
     * Signaled by DowntimeWorkflow after a recover activity. Leftover ticks carry over.
     */
    @SignalMethod
    void applyRecovery(int segments, Harm.RecoveryChoice choice);

    /**
     * Signaled by DowntimeWorkflow with long-term project progress.
     */
    @SignalMethod
    void applyProjectProgress(String clockName, int segments);

    /** Signaled when a new personal long-term project clock is started. */
    @SignalMethod
    void startProject(String name, int segments);

    /** Signaled when the PC (not the crew) acquires an asset. */
    @SignalMethod
    void addPersonalAsset(String asset);

    /** Kill, retire, or replace the character; run() returns instead of continueAsNew. */
    @SignalMethod
    void endCharacter();

    /** Read-only snapshot for UI/GM tooling. */
    @QueryMethod
    Player getState();

    @QueryMethod
    java.util.Map<String, aigm.gamestate.Clock> getProjects();

    @QueryMethod
    java.util.List<String> getPersonalAssets();

    @QueryMethod
    boolean needsTraumaChoice();

    @UpdateMethod
    CreationPrompt choosePlaybook(String playbookName);

    @UpdateMethod
    CreationPrompt chooseHeritage(Heritage heritage, String detail);

    @UpdateMethod
    CreationPrompt chooseBackground(Background background, String detail);

    @UpdateMethod
    CreationPrompt assignActionDot(Action action);

    @UpdateMethod
    CreationPrompt chooseAbility(String abilityName);

    @UpdateMethod
    CreationPrompt chooseContacts(String friendName, String rivalName);

    @UpdateMethod
    CreationPrompt chooseVice(ViceKind kind, String purveyor);

    @UpdateMethod
    CreationPrompt setIdentity(String name, String alias, String look);

    @QueryMethod
    CreationPrompt getCreationPrompt();

    @QueryMethod
    boolean isCreationComplete();

    @QueryMethod
    PcCreationStep getCreationStep();
}
