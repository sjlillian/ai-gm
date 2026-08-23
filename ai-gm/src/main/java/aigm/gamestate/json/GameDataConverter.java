package aigm.gamestate.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import aigm.gamestate.Ability;
import aigm.gamestate.campaign.Claim;
import aigm.gamestate.campaign.CrewType;
import aigm.gamestate.campaign.Upgrade;
import aigm.gamestate.player.Item;
import aigm.gamestate.player.Playbook;
import io.temporal.common.converter.DataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.common.converter.JacksonJsonPayloadConverter;

/**
 * Temporal payload converter that can round-trip campaign sheets.
 * Catalog interfaces serialize as enum names or custom records; computed
 * getters like {@code Clock.isComplete()} must not fail replay.
 */
public final class GameDataConverter {

    private GameDataConverter() {}

    public static DataConverter create() {
        return DefaultDataConverter.newDefaultInstance()
            .withPayloadConverterOverrides(new JacksonJsonPayloadConverter(objectMapper()));
    }

    static ObjectMapper objectMapper() {
        ObjectMapper mapper = JacksonJsonPayloadConverter.newDefaultObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SimpleModule catalog = new SimpleModule("bitd-catalog");
        catalog.addDeserializer(CrewType.class, new CatalogDeserializers.CrewTypeDeserializer());
        catalog.addDeserializer(Ability.class, new CatalogDeserializers.AbilityDeserializer());
        catalog.addDeserializer(Upgrade.class, new CatalogDeserializers.UpgradeDeserializer());
        catalog.addDeserializer(Claim.class, new CatalogDeserializers.ClaimDeserializer());
        catalog.addDeserializer(Playbook.class, new CatalogDeserializers.PlaybookDeserializer());
        catalog.addDeserializer(Item.class, new CatalogDeserializers.ItemDeserializer());
        mapper.registerModule(catalog);
        return mapper;
    }
}
