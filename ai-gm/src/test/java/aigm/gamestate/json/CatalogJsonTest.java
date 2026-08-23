package aigm.gamestate.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import aigm.client.DemoCrews;
import aigm.gamestate.Ability;
import aigm.gamestate.AbilityCustom;
import aigm.gamestate.AbilityEnum;
import aigm.gamestate.campaign.ClaimCustom;
import aigm.gamestate.campaign.ClaimEnum;
import aigm.gamestate.campaign.Crew;
import aigm.gamestate.campaign.CrewTypeCustom;
import aigm.gamestate.campaign.CrewTypeEnum;
import aigm.gamestate.campaign.UpgradeCustom;
import aigm.gamestate.campaign.UpgradeEnum;
import aigm.gamestate.player.ItemCustom;
import aigm.gamestate.player.ItemEnum;
import aigm.gamestate.player.PlaybookCustom;
import aigm.gamestate.player.PlaybookEnum;
import aigm.gamestate.player.Player;
import aigm.workflow.CampaignState;
import io.temporal.api.common.v1.Payload;
import io.temporal.common.converter.DataConverter;
import io.temporal.common.converter.JacksonJsonPayloadConverter;

class CatalogJsonTest {

    private final DataConverter converter = GameDataConverter.create();

    @Test
    void roundTripsDemoCampaignState() {
        CampaignState original = CampaignState.initial(DemoCrews.nightspires());
        CampaignState restored = roundTrip(original, CampaignState.class);

        assertEquals(CrewTypeEnum.SHADOWS, restored.crew().type());
        assertEquals(PlaybookEnum.LURK, restored.crew().members().get(0).playbook());
        assertEquals(original.crew().name(), restored.crew().name());
        assertEquals("Ilyas", restored.crew().members().get(0).name());
    }

    @Test
    void roundTripsCustomCatalogTypes() {
        CrewTypeCustom type = new CrewTypeCustom(
            "Ghosts",
            "When you pull off a score in your hunting grounds",
            List.of(new UpgradeCustom("Safehouse", "A bolt-hole", 1)),
            List.of(new AbilityCustom("Ghost Echo", "Leave no trace", Ability.Scope.CREW)),
            List.of(new ClaimCustom("Whispers", "Rumors find you", "info"))
        );
        Crew original = DemoCrews.nightspires();
        original = new Crew(
            original.name(),
            type,
            original.lair(),
            original.huntingGrounds(),
            original.members(),
            original.coin(),
            original.heat(),
            original.crewStanding(),
            original.crewXP(),
            List.of(AbilityEnum.ABILITY_1, new AbilityCustom("Home brew", "custom", Ability.Scope.CREW)),
            List.of(UpgradeEnum.HIDEOUT, new UpgradeCustom("Workshop", "tools", 2)),
            original.contacts(),
            List.of(ClaimEnum.CLAIM_1, new ClaimCustom("Dock", "a pier", "turf")),
            original.scores(),
            original.clocks(),
            original.factionStatuses()
        );

        Crew restored = roundTrip(original, Crew.class);
        assertInstanceOf(CrewTypeCustom.class, restored.type());
        assertEquals(type, restored.type());
        assertEquals(AbilityEnum.ABILITY_1, restored.abilities().get(0));
        assertInstanceOf(AbilityCustom.class, restored.abilities().get(1));
        assertEquals(UpgradeEnum.HIDEOUT, restored.upgrades().get(0));
        assertEquals(ClaimEnum.CLAIM_1, restored.claims().get(0));
    }

    @Test
    void roundTripsCustomPlaybookAndItems() {
        Player original = DemoCrews.nightspires().members().get(0)
            .withPlaybook(new PlaybookCustom(
                "Ghost",
                Map.of(),
                List.of(AbilityEnum.ABILITY_2),
                List.of(ItemEnum.GADGET, new ItemCustom("Lockpicks", "quiet entry", true)),
                List.of("When you infiltrate")
            ));

        Player restored = roundTrip(original, Player.class);
        assertInstanceOf(PlaybookCustom.class, restored.playbook());
        assertEquals("Ghost", restored.playbook().getName());
        assertEquals(ItemEnum.GADGET, restored.playbook().getAvailableItems().get(0));
        assertInstanceOf(ItemCustom.class, restored.playbook().getAvailableItems().get(1));
    }

    @Test
    void replaysPayloadsWrittenByTheDefaultConverter() {
        CampaignState original = CampaignState.initial(DemoCrews.nightspires());
        Payload payload = new JacksonJsonPayloadConverter().toData(original).orElseThrow();
        CampaignState restored = converter.fromPayload(payload, CampaignState.class, CampaignState.class);

        assertEquals(CrewTypeEnum.SHADOWS, restored.crew().type());
        assertEquals(PlaybookEnum.LURK, restored.crew().members().get(0).playbook());
        assertEquals("The Nightspires", restored.crew().name());
    }

    private <T> T roundTrip(T value, Class<T> type) {
        Payload payload = converter.toPayload(value).orElseThrow();
        return converter.fromPayload(payload, type, type);
    }
}
