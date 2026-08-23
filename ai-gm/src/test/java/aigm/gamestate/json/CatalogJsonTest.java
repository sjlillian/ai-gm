package aigm.gamestate.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import aigm.client.DemoCrews;
import aigm.gamestate.Ability;
import aigm.gamestate.AbilityCustom;
import aigm.gamestate.Contact;
import aigm.gamestate.ContactCustom;
import aigm.gamestate.campaign.CrewAbilityEnum;
import aigm.gamestate.campaign.ClaimCustom;
import aigm.gamestate.campaign.ClaimEnum;
import aigm.gamestate.campaign.Crew;
import aigm.gamestate.campaign.CrewContactEnum;
import aigm.gamestate.campaign.CrewTypeCustom;
import aigm.gamestate.campaign.CrewTypeEnum;
import aigm.gamestate.campaign.UpgradeCustom;
import aigm.gamestate.campaign.UpgradeEnum;
import aigm.gamestate.player.ItemCustom;
import aigm.gamestate.player.ItemEnum;
import aigm.gamestate.player.PlaybookCustom;
import aigm.gamestate.player.PlaybookEnum;
import aigm.gamestate.player.PlayerAbilityEnum;
import aigm.gamestate.player.PlayerContactEnum;
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
            List.of(new ContactCustom("Mara", "A whisper-monger.", Contact.Scope.CREW)),
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
            List.of(CrewAbilityEnum.DEADLY, new AbilityCustom("Home brew", "custom", Ability.Scope.CREW)),
            List.of(UpgradeEnum.HIDDEN_LAIR, new UpgradeCustom("Safehouse", "tools", 2)),
            List.of(CrewContactEnum.SHADOWS_DOWLER, new ContactCustom("Mara", "A whisper-monger.", Contact.Scope.CREW)),
            List.of(ClaimEnum.INFORMANTS, new ClaimCustom("Dock", "a pier", "turf")),
            original.scores(),
            original.clocks(),
            original.cohorts(),
            original.factionStatuses()
        );

        Crew restored = roundTrip(original, Crew.class);
        assertInstanceOf(CrewTypeCustom.class, restored.type());
        assertEquals(type, restored.type());
        assertEquals(CrewAbilityEnum.DEADLY, restored.abilities().get(0));
        assertInstanceOf(AbilityCustom.class, restored.abilities().get(1));
        assertEquals(UpgradeEnum.HIDDEN_LAIR, restored.upgrades().get(0));
        assertEquals(CrewContactEnum.SHADOWS_DOWLER, restored.contacts().get(0));
        assertInstanceOf(ContactCustom.class, restored.contacts().get(1));
        assertEquals(ClaimEnum.INFORMANTS, restored.claims().get(0));
    }

    @Test
    void roundTripsCustomPlaybookAndItems() {
        Player original = DemoCrews.nightspires().members().get(0)
            .withPlaybook(new PlaybookCustom(
                "Ghost",
                Map.of(),
                List.of(PlayerAbilityEnum.INFILTRATOR),
                List.of(ItemEnum.BURGLARY_GEAR, new ItemCustom("Lockpicks", "quiet entry", 0, true)),
                List.of(PlayerContactEnum.LURK_TELDA),
                List.of("When you infiltrate")
            ));

        Player restored = roundTrip(original, Player.class);
        assertInstanceOf(PlaybookCustom.class, restored.playbook());
        assertEquals("Ghost", restored.playbook().getName());
        assertEquals(ItemEnum.BURGLARY_GEAR, restored.playbook().getAvailableItems().get(0));
        assertInstanceOf(ItemCustom.class, restored.playbook().getAvailableItems().get(1));
        assertEquals(PlayerContactEnum.LURK_TELDA, restored.playbook().getAvailableContacts().get(0));
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
