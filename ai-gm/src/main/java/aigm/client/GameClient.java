package aigm.client;

import aigm.gamestate.campaign.Crew;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import aigm.llm.gm.LlmActivities;
import aigm.workflow.ActionRollResult;
import aigm.workflow.DowntimeActivityChoice;

/**
 * Backend-agnostic game API. CLI / Discord / web should depend only on this.
 * <p>
 * Implementations talk to Temporal (or a fake for tests). No stdin/stdout here.
 */
public interface GameClient extends AutoCloseable {

    /** Starts a new campaign workflow. Returns the campaign workflow id. */
    String startCampaign(Crew crew, String campaignIdOrNull);

    /** Attaches to an already-running campaign. */
    void attach(String campaignWorkflowId);

    String campaignId();

    CampaignSnapshot snapshot();

    void startScore(StartScoreCommand command);

    LlmActivities.Adjudication adjudicate(String situation, String approach, Action action);

    ActionRollResult resolveAction(
        String pcId,
        Action action,
        int actionRating,
        aigm.gamestate.Position position,
        aigm.gamestate.Effect effect,
        boolean push,
        boolean assist,
        String consequence
    );

    void tickClock(String clockId, int segments);

    void endScore(EndScoreCommand command);

    void chooseDowntimeActivity(String pcId, DowntimeActivityChoice choice);

    void closeDowntime();

    Player getPlayer(String pcNameOrWorkflowId);

    void markTrauma(String pcNameOrWorkflowId, Trauma.Condition condition);

    void endCampaign();

    @Override
    void close();
}
