package aigm.gamestate.json;

import aigm.gamestate.Ability;
import aigm.gamestate.AbilityCustom;
import aigm.gamestate.AbilityEnum;
import aigm.gamestate.campaign.Claim;
import aigm.gamestate.campaign.ClaimCustom;
import aigm.gamestate.campaign.ClaimEnum;
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

/** Concrete Jackson deserializers for catalog interfaces used in Temporal payloads. */
public final class CatalogDeserializers {

    private CatalogDeserializers() {}

    public static final class CrewTypeDeserializer
            extends EnumOrObjectDeserializer<CrewType, CrewTypeEnum, CrewTypeCustom> {
        public CrewTypeDeserializer() {
            super(CrewTypeEnum.class, CrewTypeCustom.class);
        }
    }

    public static final class AbilityDeserializer
            extends EnumOrObjectDeserializer<Ability, AbilityEnum, AbilityCustom> {
        public AbilityDeserializer() {
            super(AbilityEnum.class, AbilityCustom.class);
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
