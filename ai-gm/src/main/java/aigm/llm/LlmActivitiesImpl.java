package aigm.llm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.RelationshipStatus;
import aigm.gamestate.player.Action;
import io.temporal.activity.Activity;
import io.temporal.failure.ApplicationFailure;

/**
 * Wraps {@link LlmClient} for Temporal. Maps retryable LLM errors to retryable
 * activity failures and auth/validation errors to non-retryable ones.
 */
public class LlmActivitiesImpl implements LlmActivities {

    private static final Logger log = LoggerFactory.getLogger(LlmActivitiesImpl.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final LlmClient client;

    public LlmActivitiesImpl(LlmClient client) {
        this.client = client;
    }

    @Override
    public Adjudication adjudicateAction(String situation, String approach, Action chosenAction) {
        LlmResponse response = invoke(GmPrompts.adjudicate(situation, approach, chosenAction));
        return parseAdjudication(response.content(), chosenAction);
    }

    @Override
    public String narrate(String situation, String mechanicalOutcome) {
        LlmResponse response = invoke(GmPrompts.narrate(situation, mechanicalOutcome));
        String text = response.content().trim();
        if (text.isEmpty()) {
            throw LlmException.retryable("LLM returned empty narration", 200);
        }
        return text;
    }

    @Override
    public StartingSituation generateStartingSituation(String crewSummary) {
        LlmResponse response = invoke(GmPrompts.startingSituation(crewSummary));
        return parseStartingSituation(response.content());
    }

    StartingSituation parseStartingSituation(String raw) {
        JsonNode node;
        try {
            node = MAPPER.readTree(extractJsonObject(raw));
        } catch (IOException e) {
            throw LlmException.retryable("LLM starting situation was not JSON: " + e.getMessage(), e);
        }
        String fiction = text(node, "fiction");
        if (fiction.isBlank()) {
            throw LlmException.retryable("LLM starting situation missing fiction", 200);
        }
        List<ClockSpec> clocks = new ArrayList<>();
        JsonNode clocksNode = node.get("clocks");
        if (clocksNode != null && clocksNode.isArray()) {
            clocksNode.forEach(item -> {
                String name = item.path("name").asText("");
                int segments = item.path("segments").asInt(4);
                if (!name.isBlank()) {
                    clocks.add(new ClockSpec(name, Math.max(4, segments)));
                }
            });
        }
        if (clocks.isEmpty()) {
            clocks.add(new ClockSpec("Opening Trouble", 6));
            clocks.add(new ClockSpec("Faction Pressure", 4));
        }
        List<FactionNote> factions = new ArrayList<>();
        JsonNode factionsNode = node.get("factions");
        if (factionsNode != null && factionsNode.isArray()) {
            factionsNode.forEach(item -> {
                String faction = item.path("faction").asText("");
                RelationshipStatus status = parseEnum(
                    RelationshipStatus.class,
                    item.path("status").asText(""),
                    RelationshipStatus.NEUTRAL
                );
                if (!faction.isBlank()) {
                    factions.add(new FactionNote(faction, status));
                }
            });
        }
        List<ScoreSeed> scores = new ArrayList<>();
        JsonNode scoresNode = node.get("scores");
        if (scoresNode != null && scoresNode.isArray()) {
            scoresNode.forEach(item -> {
                String title = item.path("title").asText("");
                if (title.isBlank()) {
                    return;
                }
                scores.add(new ScoreSeed(
                    title,
                    item.path("hook").asText(""),
                    item.path("targetName").asText(""),
                    item.path("targetTier").asText("ZERO"),
                    item.path("planType").asText("STEALTH"),
                    item.path("district").asText("")
                ));
            });
        }
        return new StartingSituation(fiction, clocks, factions, scores);
    }

    private LlmResponse invoke(LlmRequest request) {
        heartbeat("llm " + client.describe());
        try {
            LlmResponse response = client.complete(request);
            log.info(
                "LLM {} tokens prompt={} completion={} total={}",
                response.model(),
                response.usage().promptTokens(),
                response.usage().completionTokens(),
                response.usage().totalTokens()
            );
            return response;
        } catch (LlmException e) {
            throw toTemporal(e);
        }
    }

    Adjudication parseAdjudication(String raw, Action chosenAction) {
        JsonNode node;
        try {
            node = MAPPER.readTree(extractJsonObject(raw));
        } catch (IOException e) {
            throw LlmException.retryable("LLM adjudication was not JSON: " + e.getMessage(), e);
        }
        Action action = chosenAction != null
            ? chosenAction
            : parseEnum(Action.class, text(node, "action"), Action.PROWL);
        Position position = parseEnum(Position.class, text(node, "position"), Position.RISKY);
        Effect effect = parseEnum(Effect.class, text(node, "effect"), Effect.STANDARD);
        List<String> stakes = new ArrayList<>();
        JsonNode stakesNode = node.get("possibleStakes");
        if (stakesNode != null && stakesNode.isArray()) {
            stakesNode.forEach(item -> {
                if (item.isTextual() && !item.asText().isBlank()) {
                    stakes.add(item.asText());
                }
            });
        }
        if (stakes.isEmpty()) {
            stakes.add("Complication");
            stakes.add("Harm");
            stakes.add("Clock advances");
            stakes.add("Worse position");
        }
        return new Adjudication(action, position, effect, text(node, "reasoning"), stakes);
    }

    private static String extractJsonObject(String text) {
        if (text == null || text.isBlank()) {
            return "{}";
        }
        String trimmed = text.trim();
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? "" : value.asText();
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String raw, E fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT).replace(' ', '_').replace('-', '_');
        try {
            return Enum.valueOf(type, normalized);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private static RuntimeException toTemporal(LlmException e) {
        if (e.retryable()) {
            return e;
        }
        return ApplicationFailure.newNonRetryableFailure(
            e.getMessage(),
            LlmException.class.getName()
        );
    }

    private static void heartbeat(String detail) {
        try {
            Activity.getExecutionContext().heartbeat(detail);
        } catch (Exception ignored) {
            // Unit tests (and any non-activity caller) have no activity context.
        }
    }
}
