package aigm.starters;

import aigm.client.DemoCrews;
import aigm.client.temporal.TemporalGameClient;
import aigm.gamestate.campaign.Crew;
import io.temporal.client.WorkflowClient;

/**
 * Thin Temporal bootstrap helpers. Prefer {@link aigm.client.GameClient} for play.
 */
public class GameStarter {

    private final TemporalGameClient game;

    public GameStarter() {
        this.game = new TemporalGameClient();
    }

    public GameStarter(WorkflowClient client) {
        this.game = new TemporalGameClient(client);
    }

    public String startCampaign(Crew crew, String campaignId) {
        return game.startCampaign(crew, campaignId);
    }

    /** Demo entry used by {@link aigm.App}. */
    public void run() {
        String id = startCampaign(DemoCrews.nightspires(), "campaign-demo");
        System.out.println("Started campaign workflow: " + id);
        System.out.println("Run the CLI for play: mvn exec:java -Dexec.mainClass=aigm.client.cli.GameCli");
    }
}
