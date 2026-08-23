package aigm.llm.gm;

import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.player.Action;
import aigm.llm.LlmRequest;

/** BitD-flavored prompts. Mechanics stay in Java; the model only judges fiction. */
final class GmPrompts {

    private GmPrompts() {}

    static LlmRequest adjudicate(String situation, String approach, Action chosenAction) {
        String actionLine = chosenAction == null
            ? "The player has not locked an action rating yet. Suggest one that fits the approach."
            : "The player chose action rating: " + chosenAction + " (" + chosenAction.getAttribute() + "). "
                + "Keep that action unless it cannot possibly achieve the stated approach.";
        String user = """
            Situation:
            %s

            Player approach:
            %s

            %s

            Reply with a single JSON object using these keys:
            {
              "action": one of %s,
              "position": one of CONTROLLED, RISKY, DESPERATE,
              "effect": one of LIMITED, STANDARD, GREAT,
              "reasoning": short GM explanation of position and effect,
              "possibleStakes": array of 2-4 concrete consequences (harm, clock, complication, worse position, lost opportunity)
            }
            Do not roll dice. Do not decide success or failure.
            """.formatted(
                nullToEmpty(situation),
                nullToEmpty(approach),
                actionLine,
                actionList()
            );
        return LlmRequest.builder()
            .addSystem(adjudicationSystem())
            .addUser(user)
            .temperature(0.3)
            .maxTokens(700)
            .jsonObject(true)
            .build();
    }

    static LlmRequest narrate(String situation, String mechanicalOutcome) {
        String user = """
            Situation:
            %s

            Mechanical outcome (already resolved — do not contradict it):
            %s

            Narrate what the table sees and hears next. 2-5 sentences. End on a prompt for the players.
            """.formatted(nullToEmpty(situation), nullToEmpty(mechanicalOutcome));
        return LlmRequest.builder()
            .addSystem(narrationSystem())
            .addUser(user)
            .temperature(0.8)
            .maxTokens(500)
            .jsonObject(false)
            .build();
    }

    private static String adjudicationSystem() {
        return """
            You are the GM for Blades in the Dark (2017 Core Rulebook). Fiction is primary.
            Set position and effect from the established situation before any dice.
            Do not invent hidden canonical facts about Doskvol; offer a ruling and say why.

            Position (danger):
            %s
            Effect (how much this can accomplish; clocks often tick limited 1 / standard 2 / great 3):
            %s
            Guardrails:
            - The player chooses the action rating; only change it if the stated action cannot achieve the approach.
            - A 4-5 is still a success; consequences create a new situation rather than erasing it.
            - Offer bargains with real teeth, never as a disguised refusal.
            - Keep NPCs competent. No secret immunity or forced failure.
            """.formatted(enumLines(Position.values()), enumLines(Effect.values()));
    }

    private static String narrationSystem() {
        return """
            You are the GM for Blades in the Dark in Doskvol: industrial, haunted, rain-slick, lamplit.
            Narrate in second person toward the scoundrels. Be a fan of their daring.
            Stay consistent with the mechanical outcome you are given. Do not roll or invent sheet changes.
            Keep unknowns unknown. Concrete sensory detail over lore dumps.
            """;
    }

    private static String actionList() {
        StringBuilder sb = new StringBuilder();
        Action[] actions = Action.values();
        for (int i = 0; i < actions.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(actions[i].name());
        }
        return sb.toString();
    }

    private static String enumLines(Enum<?>[] values) {
        StringBuilder sb = new StringBuilder();
        for (Enum<?> value : values) {
            sb.append("- ").append(value.name()).append(": ");
            if (value instanceof Position position) {
                sb.append(position.getDescription());
            } else if (value instanceof Effect effect) {
                sb.append(effect.getDescription());
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
