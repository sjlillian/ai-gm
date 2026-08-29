package aigm.client.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import aigm.client.CampaignSnapshot;
import aigm.gamestate.campaign.Crew;
import aigm.gamestate.player.Player;
import aigm.workflow.CampaignWorkflow;
import aigm.workflow.CreationPrompt;
import aigm.workflow.CrewCreationStep;
import aigm.workflow.SessionZeroStatus;

class UiAssemblerTest {

    @Test
    void joinIdsStripCampaignPrefix() {
        assertEquals(
            "alice",
            UiAssembler.joinId("campaign-demo", "pc-campaign-demo-alice")
        );
    }

    @Test
    void waitingForJoinIsNotRespondableAndOffersJoinActions() {
        CampaignSnapshot snapshot = snapshot(
            CampaignWorkflow.Phase.SESSION_ZERO,
            List.of("pc-campaign-demo-alice"),
            new SessionZeroStatus(
                CrewCreationStep.WAITING_FOR_JOIN,
                false,
                List.of("alice"),
                List.of(),
                0
            ),
            CreationPrompt.of(CrewCreationStep.WAITING_FOR_JOIN, "Join scoundrels", List.of())
        );

        UiView view = UiAssembler.assemble(snapshot, "campaign", Map.of(), Map.of());

        assertEquals(2, view.clients().size());
        assertEquals("alice", view.clients().get(1).id());
        assertTrue(view.respondable());
        assertEquals(List.of("ready", "end"), view.actions().stream().map(UiAction::id).toList());
    }

    @Test
    void crewTypePromptIsRespondable() {
        CampaignSnapshot snapshot = snapshot(
            CampaignWorkflow.Phase.SESSION_ZERO,
            List.of(),
            new SessionZeroStatus(CrewCreationStep.TYPE, true, List.of("alice"), List.of("alice"), 0),
            CreationPrompt.of(CrewCreationStep.TYPE, "Choose a crew type.", List.of("SHADOWS", "HAWKERS"))
        );

        UiView view = UiAssembler.assemble(snapshot, UiClient.CAMPAIGN, Map.of(), Map.of());

        assertTrue(view.respondable());
        assertEquals("SHADOWS", view.selected().prompt().options().get(0));
    }

    @Test
    void selectedPcUsesItsOwnPromptAndSheet() {
        CampaignSnapshot snapshot = snapshot(
            CampaignWorkflow.Phase.SESSION_ZERO,
            List.of("pc-campaign-demo-alice"),
            new SessionZeroStatus(
                CrewCreationStep.WAITING_FOR_PCS,
                true,
                List.of("alice"),
                List.of(),
                0
            ),
            CreationPrompt.of(CrewCreationStep.WAITING_FOR_PCS, "Waiting", List.of())
        );
        Player sheet = Player.draft("alice");
        CreationPrompt prompt = CreationPrompt.of(
            aigm.workflow.PcCreationStep.PLAYBOOK,
            "Choose a playbook.",
            List.of("CUTTER", "LURK")
        );

        UiView view = UiAssembler.assemble(
            snapshot,
            "alice",
            Map.of("alice", sheet),
            Map.of("alice", prompt)
        );

        assertEquals("alice", view.selected().id());
        assertEquals("PC", view.selected().kind());
        assertTrue(view.respondable());
        assertEquals("CUTTER", view.selected().prompt().options().get(0));
        assertEquals(sheet, view.selected().state());
    }

    @Test
    void freeplayCampaignOffersStartScore() {
        CampaignSnapshot snapshot = snapshot(
            CampaignWorkflow.Phase.FREEPLAY,
            List.of(),
            null,
            CreationPrompt.done("Session 0 is complete.")
        );

        UiView view = UiAssembler.assemble(snapshot, UiClient.CAMPAIGN, Map.of(), Map.of());

        assertFalse(view.respondable());
        assertEquals("investigate", view.actions().get(0).id());
        assertEquals("score", view.actions().get(1).id());
    }

    @Test
    void splitsFreeformResponseWhenTokenIsBlank() {
        assertEquals("Crow's", UiAssembler.normalizeToken("", "Crow's Foot loft"));
        assertEquals("Foot loft", UiAssembler.normalizeRest("", "Crow's Foot loft"));
        assertEquals("SHADOWS", UiAssembler.normalizeToken("SHADOWS", "ignored"));
        assertEquals("detail", UiAssembler.normalizeRest("AKOROS", "detail"));
    }

    private static CampaignSnapshot snapshot(
        CampaignWorkflow.Phase phase,
        List<String> pcWorkflowIds,
        SessionZeroStatus sessionZero,
        CreationPrompt prompt
    ) {
        return new CampaignSnapshot(
            "campaign-demo",
            phase,
            0,
            Crew.blank(),
            List.of(),
            pcWorkflowIds,
            null,
            null,
            null,
            Map.of(),
            null,
            Map.of(),
            sessionZero,
            prompt,
            "",
            List.of(),
            "",
            ""
        );
    }
}
