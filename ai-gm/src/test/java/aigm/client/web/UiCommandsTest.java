package aigm.client.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import aigm.gamestate.campaign.CrewStanding;

class UiCommandsTest {

    @Test
    void readsFieldTypesFromJsonShapedMaps() {
        Map<String, Object> fields = Map.of(
            "title", "The Vault",
            "engagementDice", 3,
            "push", true,
            "pay", "on"
        );
        assertEquals("The Vault", UiCommands.str(fields, "title"));
        assertEquals(3, UiCommands.num(fields, "engagementDice"));
        assertTrue(UiCommands.bool(fields, "push"));
        assertTrue(UiCommands.bool(fields, "pay"));
        assertFalse(UiCommands.bool(fields, "assist"));
        assertEquals(2, UiCommands.optionalNum(fields, "baseHeat", 2));
    }

    @Test
    void parseTierAcceptsOrdinalOrName() {
        assertEquals(CrewStanding.Tier.TWO, UiCommands.parseTier("2"));
        assertEquals(CrewStanding.Tier.THREE, UiCommands.parseTier("THREE"));
    }

    @Test
    void missingRequiredFieldFails() {
        assertThrows(IllegalArgumentException.class, () -> UiCommands.str(Map.of(), "pcId"));
    }
}
