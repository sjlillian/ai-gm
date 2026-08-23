package aigm.llm.gm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.player.Action;
import aigm.llm.stub.StubLlmClient;

class LlmActivitiesImplTest {

    private final LlmActivitiesImpl activities = new LlmActivitiesImpl(new StubLlmClient());

    @Test
    void stubAdjudicationKeepsPlayerAction() {
        LlmActivities.Adjudication result = activities.adjudicateAction(
            "Rooftop above the lampblack den",
            "Slip past the lookout",
            Action.PROWL
        );
        assertEquals(Action.PROWL, result.action());
        assertEquals(Position.RISKY, result.position());
        assertEquals(Effect.STANDARD, result.effect());
        assertTrue(result.possibleStakes().size() >= 2);
    }

    @Test
    void parsesMessyJsonAndPrefersChosenAction() {
        String raw = """
            Here you go:
            ```json
            {"action":"SKIRMISH","position":"desperate","effect":"great","reasoning":"They're already in the melee.","possibleStakes":["Level 2 harm","Alert clock +2"]}
            ```
            """;
        LlmActivities.Adjudication result = activities.parseAdjudication(raw, Action.WRECK);
        assertEquals(Action.WRECK, result.action());
        assertEquals(Position.DESPERATE, result.position());
        assertEquals(Effect.GREAT, result.effect());
        assertEquals("They're already in the melee.", result.reasoning());
        assertEquals(2, result.possibleStakes().size());
    }

    @Test
    void stubNarrationIsNonEmpty() {
        String text = activities.narrate("The engagement roll is a 6", "Starting position: Controlled");
        assertTrue(text.contains("Doskvol") || text.contains("lamps") || text.length() > 10);
    }
}
