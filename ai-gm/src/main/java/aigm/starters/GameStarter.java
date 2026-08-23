package aigm.starters;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import aigm.TaskQueues;
import aigm.gamestate.Ability;
import aigm.gamestate.Clock;
import aigm.gamestate.campaign.Crew;
import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.campaign.CrewTypeEnum;
import aigm.gamestate.campaign.Heat;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Advancement;
import aigm.gamestate.player.Harm;
import aigm.gamestate.player.Loadout;
import aigm.gamestate.player.PlaybookEnum;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import aigm.gamestate.player.Vice;
import aigm.workflow.CampaignState;
import aigm.workflow.CampaignWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

/**
 * Client entrypoints that connect Discord/web/CLI to Temporal workflows.
 */
public class GameStarter {

    private final WorkflowClient client;

    public GameStarter() {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        this.client = WorkflowClient.newInstance(service);
    }

    public GameStarter(WorkflowClient client) {
        this.client = client;
    }

    /** Starts a long-running campaign workflow (async). */
    public String startCampaign(Crew crew, String campaignId) {
        String workflowId = campaignId == null || campaignId.isBlank()
            ? "campaign-" + UUID.randomUUID()
            : campaignId;

        CampaignWorkflow workflow = client.newWorkflowStub(
            CampaignWorkflow.class,
            WorkflowOptions.newBuilder()
                .setTaskQueue(TaskQueues.GAME)
                .setWorkflowId(workflowId)
                .build()
        );

        WorkflowClient.start(workflow::run, CampaignState.initial(crew));
        return workflowId;
    }

    /** Demo entry used by {@link aigm.App}. */
    public void run() {
        String id = startCampaign(demoCrew(), "campaign-demo");
        System.out.println("Started campaign workflow: " + id);
    }

    static Crew demoCrew() {
        Player scoundrel = new Player(
            "Ilyas",
            "Akoros",
            "Underworld",
            new Vice("Gambling", "The Six Towers dens"),
            new Trauma(),
            Map.of(
                Action.PROWL, 2,
                Action.FINESSE, 1,
                Action.SKIRMISH, 1
            ),
            new Harm(),
            1,
            0,
            PlaybookEnum.LURK,
            List.<Ability>of(),
            new Advancement(),
            new Loadout()
        );

        return new Crew(
            "The Nightspires",
            CrewTypeEnum.SHADOWS,
            "Crow's Foot loft",
            "Crow's Foot",
            List.of(scoundrel),
            2,
            new Heat(),
            new CrewStanding(),
            new Clock("Crew XP", Crew.CREW_XP_BOXES),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            Map.of()
        );
    }
}
