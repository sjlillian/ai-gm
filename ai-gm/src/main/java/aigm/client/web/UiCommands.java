package aigm.client.web;

import java.util.List;
import java.util.Map;

import aigm.client.CampaignSnapshot;
import aigm.client.TemporalGameClient;
import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Trauma;
import aigm.gamestate.score.ScoreType;
import aigm.llm.LlmActivities;
import aigm.workflow.ActionRollResult;
import aigm.workflow.CreationPrompt;
import aigm.workflow.DowntimeActivityChoice;
import aigm.workflow.ScoreEndRequest;
import aigm.workflow.ScoreRequest;

/** Maps schema-driven UI posts onto {@link TemporalGameClient}. */
public final class UiCommands {

    private UiCommands() {}

    public static CreationPrompt respond(
        TemporalGameClient game,
        String clientId,
        String token,
        String rest,
        Map<String, String> fields
    ) {
        Map<String, String> extra = fields == null ? Map.of() : fields;
        String choice = firstNonBlank(extra.get("token"), UiAssembler.normalizeToken(token, rest));
        String details = firstNonBlank(extra.get("detail"), UiAssembler.normalizeRest(token, rest));
        if (choice.isBlank() && extra.values().stream().noneMatch(v -> v != null && !v.isBlank())) {
            throw new IllegalArgumentException("A response is required");
        }
        return game.applyResponse(clientId, choice, details, extra);
    }

    public static CreationPrompt respond(TemporalGameClient game, String clientId, String token, String rest) {
        return respond(game, clientId, token, rest, Map.of());
    }

    public static Object execute(
        TemporalGameClient game,
        String actionId,
        String clientId,
        Map<String, Object> fields
    ) {
        if (actionId == null || actionId.isBlank()) {
            throw new IllegalArgumentException("action id required");
        }
        Map<String, Object> args = fields == null ? Map.of() : fields;
        return switch (actionId) {
            case "join" -> game.joinPlayer(strOr(args, "name", str(args, "pcId")));
            case "ready" -> game.closeJoining();
            case "investigate" -> game.investigate(str(args, "question"));
            case "score" -> {
                String opportunityId = strOr(args, "opportunityId", "");
                if (!opportunityId.isBlank()) {
                    aigm.workflow.ScoreOpportunity job = findOpportunity(game.snapshot(), opportunityId);
                    game.startScore(new ScoreRequest(
                        null,
                        job.title(),
                        job.planType(),
                        job.planType().name(),
                        job.targetName(),
                        job.targetTier(),
                        num(args, "engagementDice"),
                        null,
                        List.of()
                    ));
                } else {
                    game.startScore(new ScoreRequest(
                        null,
                        str(args, "title"),
                        ScoreType.valueOf(UiAssembler.upper(str(args, "planType"))),
                        UiAssembler.upper(str(args, "planType")),
                        str(args, "targetName"),
                        parseTier(str(args, "targetTier")),
                        num(args, "engagementDice"),
                        null,
                        List.of()
                    ));
                }
                yield "score started";
            }
            case "adjudicate" -> {
                LlmActivities.Adjudication adj = game.adjudicate(
                    str(args, "situation"),
                    str(args, "situation"),
                    Action.valueOf(UiAssembler.upper(str(args, "action")))
                );
                yield adj;
            }
            case "roll" -> {
                ActionRollResult roll = game.resolveAction(
                    str(args, "pcId"),
                    Action.valueOf(UiAssembler.upper(str(args, "action"))),
                    num(args, "rating"),
                    Position.valueOf(UiAssembler.upper(str(args, "position"))),
                    Effect.valueOf(UiAssembler.upper(str(args, "effect"))),
                    bool(args, "push"),
                    bool(args, "assist"),
                    ""
                );
                yield roll;
            }
            case "clock" -> {
                game.tickClock(str(args, "clockId"), num(args, "segments"));
                yield "ok";
            }
            case "endscore" -> {
                boolean success = "success".equalsIgnoreCase(str(args, "outcome"))
                    || "win".equalsIgnoreCase(str(args, "outcome"));
                int heat = optionalNum(args, "baseHeat", 2);
                CampaignSnapshot snap = game.snapshot();
                int tier = snap.crew().crewStanding().tier().ordinal();
                game.endScore(ScoreEndRequest.simple(success, tier, heat));
                yield "score ended";
            }
            case "downtime" -> {
                String pcId = strOr(args, "pcId", clientId);
                if (pcId == null || pcId.isBlank() || UiClient.CAMPAIGN.equalsIgnoreCase(pcId)) {
                    throw new IllegalArgumentException("pcId required");
                }
                game.chooseDowntimeActivity(
                    pcId,
                    new DowntimeActivityChoice(
                        DowntimeActivityChoice.Kind.valueOf(UiAssembler.upper(str(args, "kind"))),
                        pcId,
                        strOr(args, "details", ""),
                        bool(args, "pay")
                    )
                );
                yield "activity queued";
            }
            case "closedowntime" -> {
                game.closeDowntime();
                yield "downtime closed";
            }
            case "trauma" -> {
                String pcId = strOr(args, "pcId", clientId);
                if (pcId == null || pcId.isBlank() || UiClient.CAMPAIGN.equalsIgnoreCase(pcId)) {
                    throw new IllegalArgumentException("pcId required");
                }
                game.markTrauma(pcId, Trauma.Condition.valueOf(UiAssembler.upper(str(args, "condition"))));
                yield "ok";
            }
            case "end" -> {
                game.endCampaign();
                yield "campaign end signaled";
            }
            default -> throw new IllegalArgumentException("unknown action: " + actionId);
        };
    }

    static String str(Map<String, Object> fields, String key) {
        Object value = fields.get(key);
        if (value == null) {
            throw new IllegalArgumentException(key + " required");
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty() || "null".equals(text)) {
            throw new IllegalArgumentException(key + " required");
        }
        return text;
    }

    static String strOr(Map<String, Object> fields, String key, String fallback) {
        Object value = fields.get(key);
        if (value == null) {
            return fallback;
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty() || "null".equals(text)) {
            return fallback;
        }
        return text;
    }

    static int num(Map<String, Object> fields, String key) {
        Object value = fields.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(str(fields, key));
    }

    static int optionalNum(Map<String, Object> fields, String key, int fallback) {
        Object value = fields.get(key);
        if (value == null || String.valueOf(value).isBlank()) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value).trim());
    }

    static boolean bool(Map<String, Object> fields, String key) {
        Object value = fields.get(key);
        if (value instanceof Boolean flag) {
            return flag;
        }
        if (value == null) {
            return false;
        }
        String text = String.valueOf(value).trim();
        return "true".equalsIgnoreCase(text) || "on".equalsIgnoreCase(text) || "1".equals(text);
    }

    static CrewStanding.Tier parseTier(String raw) {
        try {
            int n = Integer.parseInt(raw.trim());
            CrewStanding.Tier[] values = CrewStanding.Tier.values();
            return values[Math.max(0, Math.min(n, values.length - 1))];
        } catch (NumberFormatException e) {
            return CrewStanding.Tier.valueOf(UiAssembler.upper(raw));
        }
    }

    private static aigm.workflow.ScoreOpportunity findOpportunity(CampaignSnapshot snapshot, String id) {
        String key = id.contains(" · ") ? id.substring(0, id.indexOf(" · ")).trim() : id.trim();
        if (snapshot.opportunities() != null) {
            for (aigm.workflow.ScoreOpportunity opportunity : snapshot.opportunities()) {
                if (key.equals(opportunity.id()) || key.equals(opportunity.title())) {
                    return opportunity;
                }
            }
        }
        throw new IllegalArgumentException("Unknown job: " + id);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }
}
