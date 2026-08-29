package aigm.client.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import aigm.client.CampaignSnapshot;
import aigm.gamestate.Effect;
import aigm.gamestate.Position;
import aigm.gamestate.campaign.CrewStanding;
import aigm.gamestate.player.Action;
import aigm.gamestate.player.Player;
import aigm.gamestate.player.Trauma;
import aigm.gamestate.score.ScoreType;
import aigm.workflow.CampaignWorkflow;
import aigm.workflow.CreationPrompt;
import aigm.workflow.DowntimeActivityChoice;
import aigm.workflow.SessionZeroStatus;

/**
 * Turns a {@link CampaignSnapshot} plus per-PC sheets into a UI view.
 * The browser only renders this payload; it does not know game steps.
 */
public final class UiAssembler {

    private UiAssembler() {}

    public static UiView assemble(
        CampaignSnapshot snapshot,
        String selectedId,
        Map<String, Player> pcSheets,
        Map<String, CreationPrompt> pcPrompts
    ) {
        if (snapshot == null) {
            return UiView.detached();
        }
        Map<String, Player> sheets = pcSheets == null ? Map.of() : pcSheets;
        Map<String, CreationPrompt> prompts = pcPrompts == null ? Map.of() : pcPrompts;

        List<UiClient> clients = new ArrayList<>();
        clients.add(new UiClient(
            UiClient.CAMPAIGN,
            UiClient.KIND_CAMPAIGN,
            crewLabel(snapshot),
            snapshot.creationPrompt(),
            snapshot.crew()
        ));
        for (String joinId : pcJoinIds(snapshot)) {
            Player sheet = sheets.get(joinId);
            String label = sheet != null && sheet.name() != null && !sheet.name().isBlank()
                ? sheet.name()
                : joinId;
            clients.add(new UiClient(
                joinId,
                UiClient.KIND_PC,
                label,
                prompts.get(joinId),
                sheet
            ));
        }

        UiClient selected = pick(clients, selectedId);
        boolean respondable = isRespondable(selected);
        List<UiAction> actions = actionsFor(snapshot, selected, clients);
        return new UiView(
            true,
            snapshot.campaignWorkflowId(),
            snapshot.phase(),
            snapshot.cycleNumber(),
            snapshot,
            clients,
            selected,
            respondable,
            actions
        );
    }

    static List<String> pcJoinIds(CampaignSnapshot snapshot) {
        Map<String, String> ordered = new LinkedHashMap<>();
        SessionZeroStatus sessionZero = snapshot.sessionZero();
        if (sessionZero != null) {
            for (String id : sessionZero.joinedPcIds()) {
                if (id != null && !id.isBlank()) {
                    ordered.put(id, id);
                }
            }
        }
        if (snapshot.pcWorkflowIds() != null) {
            for (String workflowId : snapshot.pcWorkflowIds()) {
                String joinId = joinId(snapshot.campaignWorkflowId(), workflowId);
                if (joinId != null && !joinId.isBlank()) {
                    ordered.put(joinId, joinId);
                }
            }
        }
        return List.copyOf(ordered.keySet());
    }

    static String joinId(String campaignWorkflowId, String pcWorkflowId) {
        if (pcWorkflowId == null || pcWorkflowId.isBlank()) {
            return pcWorkflowId;
        }
        if (campaignWorkflowId != null && !campaignWorkflowId.isBlank()) {
            String prefix = "pc-" + campaignWorkflowId + "-";
            if (pcWorkflowId.startsWith(prefix)) {
                return pcWorkflowId.substring(prefix.length());
            }
        }
        if (pcWorkflowId.startsWith("pc-")) {
            return pcWorkflowId.substring(3);
        }
        return pcWorkflowId;
    }

    static boolean isRespondable(UiClient selected) {
        if (selected == null || selected.prompt() == null || selected.prompt().complete()) {
            return false;
        }
        return !"WAITING_FOR_PCS".equals(selected.prompt().step());
    }

    private static UiClient pick(List<UiClient> clients, String selectedId) {
        if (clients.isEmpty()) {
            return null;
        }
        if (selectedId != null && !selectedId.isBlank()) {
            for (UiClient client : clients) {
                if (selectedId.equalsIgnoreCase(client.id())) {
                    return client;
                }
            }
        }
        return clients.get(0);
    }

    private static String crewLabel(CampaignSnapshot snapshot) {
        if (snapshot.crew() != null && snapshot.crew().name() != null && !snapshot.crew().name().isBlank()) {
            return snapshot.crew().name();
        }
        return "Crew";
    }

    private static List<UiAction> actionsFor(
        CampaignSnapshot snapshot,
        UiClient selected,
        List<UiClient> clients
    ) {
        if (selected == null) {
            return List.of();
        }
        List<String> pcIds = pcIds(clients);
        if (UiClient.KIND_PC.equals(selected.kind())) {
            List<UiAction> actions = new ArrayList<>();
            if (snapshot.phase() == CampaignWorkflow.Phase.DOWNTIME) {
                actions.add(downtimeAction(List.of(selected.id())));
            }
            actions.add(new UiAction(
                "trauma",
                "Mark trauma",
                List.of(UiField.select("condition", "Condition", names(Trauma.Condition.class), true))
            ));
            return List.copyOf(actions);
        }

        List<UiAction> actions = new ArrayList<>();
        CampaignWorkflow.Phase phase = snapshot.phase();
        if (phase == CampaignWorkflow.Phase.SESSION_ZERO) {
            String step = snapshot.creationPrompt() == null ? "" : snapshot.creationPrompt().step();
            if ("WAITING_FOR_JOIN".equals(step)) {
                actions.add(new UiAction("ready", "Everyone is in", List.of()));
            }
        } else if (phase == CampaignWorkflow.Phase.FREEPLAY) {
            actions.add(new UiAction(
                "investigate",
                "Investigate",
                List.of(UiField.text("question", "Ask around, watch a place, lean on a contact", true))
            ));
            List<String> jobs = opportunityIds(snapshot);
            if (!jobs.isEmpty()) {
                actions.add(new UiAction(
                    "score",
                    "Start this job",
                    List.of(
                        UiField.select("opportunityId", "Job", jobs, true),
                        UiField.number("engagementDice", "Engagement dice", true)
                    )
                ));
            } else {
                actions.add(new UiAction(
                    "score",
                    "Start a score",
                    List.of(
                        UiField.text("title", "Title", true),
                        UiField.select("planType", "Plan", names(ScoreType.class), true),
                        UiField.text("targetName", "Target", true),
                        UiField.select("targetTier", "Target tier", names(CrewStanding.Tier.class), true),
                        UiField.number("engagementDice", "Engagement dice", true)
                    )
                ));
            }
        } else if (phase == CampaignWorkflow.Phase.SCORE) {
            actions.add(new UiAction(
                "adjudicate",
                "Adjudicate",
                List.of(
                    UiField.select("action", "Action", names(Action.class), true),
                    UiField.text("situation", "Situation", true)
                )
            ));
            actions.add(new UiAction(
                "roll",
                "Action roll",
                List.of(
                    UiField.select("pcId", "PC", pcIds, true),
                    UiField.select("action", "Action", names(Action.class), true),
                    UiField.number("rating", "Rating", true),
                    UiField.select("position", "Position", names(Position.class), true),
                    UiField.select("effect", "Effect", names(Effect.class), true),
                    UiField.checkbox("push", "Push"),
                    UiField.checkbox("assist", "Assist")
                )
            ));
            actions.add(new UiAction(
                "clock",
                "Tick a clock",
                List.of(
                    UiField.text("clockId", "Clock", true),
                    UiField.number("segments", "Segments", true)
                )
            ));
            actions.add(new UiAction(
                "endscore",
                "End score",
                List.of(
                    UiField.select("outcome", "Outcome", List.of("success", "fail"), true),
                    UiField.number("baseHeat", "Base heat", false)
                )
            ));
        } else if (phase == CampaignWorkflow.Phase.DOWNTIME) {
            actions.add(downtimeAction(pcIds));
            actions.add(new UiAction("closedowntime", "Close downtime", List.of()));
        }
        actions.add(new UiAction("end", "End campaign", List.of()));
        return List.copyOf(actions);
    }

    private static UiAction downtimeAction(List<String> pcIds) {
        return new UiAction(
            "downtime",
            "Choose downtime activity",
            List.of(
                UiField.select("pcId", "Scoundrel", pcIds, true),
                UiField.select("kind", "Activity", names(DowntimeActivityChoice.Kind.class), true),
                UiField.text("details", "What are you actually doing?", false),
                UiField.checkbox("pay", "Pay extra coin for another activity")
            )
        );
    }

    private static List<String> pcIds(List<UiClient> clients) {
        List<String> ids = new ArrayList<>();
        for (UiClient client : clients) {
            if (UiClient.KIND_PC.equals(client.kind())) {
                ids.add(client.id());
            }
        }
        return ids;
    }

    private static List<String> opportunityIds(CampaignSnapshot snapshot) {
        List<String> ids = new ArrayList<>();
        if (snapshot.opportunities() != null) {
            for (aigm.workflow.ScoreOpportunity opportunity : snapshot.opportunities()) {
                ids.add(opportunity.id());
            }
        }
        return ids;
    }

    private static List<String> names(Class<? extends Enum<?>> type) {
        Enum<?>[] values = type.getEnumConstants();
        List<String> names = new ArrayList<>(values.length);
        for (Enum<?> value : values) {
            names.add(value.name());
        }
        return names;
    }

    static String normalizeToken(String token, String rest) {
        if (token != null && !token.isBlank()) {
            return token.trim();
        }
        String all = rest == null ? "" : rest.trim();
        int space = all.indexOf(' ');
        return space < 0 ? all : all.substring(0, space);
    }

    static String normalizeRest(String token, String rest) {
        if (token != null && !token.isBlank()) {
            return rest == null ? "" : rest.trim();
        }
        String all = rest == null ? "" : rest.trim();
        int space = all.indexOf(' ');
        return space < 0 ? "" : all.substring(space + 1).trim();
    }

    static String upper(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}
