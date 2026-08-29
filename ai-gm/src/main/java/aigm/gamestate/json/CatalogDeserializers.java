package aigm.gamestate.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import aigm.gamestate.Ability;
import aigm.gamestate.AbilityCustom;
import aigm.gamestate.Contact;
import aigm.gamestate.ContactCustom;
import aigm.gamestate.campaign.Claim;
import aigm.gamestate.campaign.ClaimCustom;
import aigm.gamestate.campaign.ClaimEnum;
import aigm.gamestate.campaign.CrewAbilityEnum;
import aigm.gamestate.campaign.CrewContactEnum;
import aigm.gamestate.campaign.CrewType;
import aigm.gamestate.campaign.CrewTypeCustom;
import aigm.gamestate.campaign.CrewTypeEnum;
import aigm.gamestate.campaign.Upgrade;
import aigm.gamestate.campaign.UpgradeCustom;
import aigm.gamestate.campaign.UpgradeEnum;
import aigm.gamestate.player.Item;
import aigm.gamestate.player.ItemCustom;
import aigm.gamestate.player.ItemEnum;
import aigm.gamestate.player.Playbook;
import aigm.gamestate.player.PlaybookCustom;
import aigm.gamestate.player.PlaybookEnum;
import aigm.gamestate.player.PlayerAbilityEnum;
import aigm.gamestate.player.PlayerContactEnum;

/** Concrete Jackson deserializers for catalog interfaces used in Temporal payloads. */
public final class CatalogDeserializers {

    private CatalogDeserializers() {}

    public static final class CrewTypeDeserializer
            extends EnumOrObjectDeserializer<CrewType, CrewTypeEnum, CrewTypeCustom> {
        public CrewTypeDeserializer() {
            super(CrewTypeEnum.class, CrewTypeCustom.class);
        }
    }

    /**
     * Abilities are a JSON string (player or crew enum name) or a custom object.
     * Player names are tried first; crew names second.
     */
    public static final class AbilityDeserializer extends JsonDeserializer<Ability> {
        @Override
        public Ability deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken token = p.currentToken();
            if (token == JsonToken.VALUE_NULL) {
                return getNullValue(ctxt);
            }
            if (token == JsonToken.VALUE_STRING) {
                String name = p.getText();
                try {
                    return PlayerAbilityEnum.valueOf(name);
                } catch (IllegalArgumentException ignored) {
                    // try crew catalog next
                }
                try {
                    return CrewAbilityEnum.valueOf(name);
                } catch (IllegalArgumentException e) {
                    return (Ability) ctxt.handleWeirdStringValue(Ability.class, name, e.getMessage());
                }
            }
            if (token == JsonToken.START_OBJECT) {
                return ctxt.readValue(p, AbilityCustom.class);
            }
            return (Ability) ctxt.handleUnexpectedToken(Ability.class, p);
        }
    }

    /**
     * Contacts are a JSON string (player or crew enum name) or a custom object.
     * Player names are tried first; crew names second.
     */
    public static final class ContactDeserializer extends JsonDeserializer<Contact> {
        @Override
        public Contact deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken token = p.currentToken();
            if (token == JsonToken.VALUE_NULL) {
                return getNullValue(ctxt);
            }
            if (token == JsonToken.VALUE_STRING) {
                String name = p.getText();
                try {
                    return PlayerContactEnum.valueOf(name);
                } catch (IllegalArgumentException ignored) {
                    // try crew catalog next
                }
                try {
                    return CrewContactEnum.valueOf(name);
                } catch (IllegalArgumentException e) {
                    return (Contact) ctxt.handleWeirdStringValue(Contact.class, name, e.getMessage());
                }
            }
            if (token == JsonToken.START_OBJECT) {
                return ctxt.readValue(p, ContactCustom.class);
            }
            return (Contact) ctxt.handleUnexpectedToken(Contact.class, p);
        }
    }

    public static final class UpgradeDeserializer
            extends EnumOrObjectDeserializer<Upgrade, UpgradeEnum, UpgradeCustom> {
        public UpgradeDeserializer() {
            super(UpgradeEnum.class, UpgradeCustom.class);
        }
    }

    public static final class ClaimDeserializer
            extends EnumOrObjectDeserializer<Claim, ClaimEnum, ClaimCustom> {
        public ClaimDeserializer() {
            super(ClaimEnum.class, ClaimCustom.class);
        }
    }

    public static final class PlaybookDeserializer
            extends EnumOrObjectDeserializer<Playbook, PlaybookEnum, PlaybookCustom> {
        public PlaybookDeserializer() {
            super(PlaybookEnum.class, PlaybookCustom.class);
        }
    }

    public static final class ItemDeserializer
            extends EnumOrObjectDeserializer<Item, ItemEnum, ItemCustom> {
        public ItemDeserializer() {
            super(ItemEnum.class, ItemCustom.class);
        }
    }
}
