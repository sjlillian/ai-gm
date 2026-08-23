package aigm.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import aigm.activities.Activities;
import aigm.gamestate.DiceRoll;
import aigm.gamestate.player.Advancement;
import aigm.gamestate.player.Harm;
import io.temporal.workflow.Workflow;

/**
 * One downtime phase: entanglement (heat column + wanted dice), then per-PC
 * activities until {@link #closeDowntime()}.
 */
public class DowntimeImplemented implements DowntimeWorkflow {

    private final Map<String, List<DowntimeActivityChoice>> submitted = new HashMap<>();
    private final Map<String, Integer> freeSlotsUsed = new HashMap<>();
    private final Queue<DowntimeActivityChoice> pending = new LinkedList<>();
    private boolean closed;
    private DowntimeRequest request;

    @Override
    public DowntimeResult run(DowntimeRequest request) {
        this.request = request;
        for (String pcId : request.pcIds()) {
            submitted.put(pcId, new ArrayList<>());
            freeSlotsUsed.put(pcId, 0);
        }

        Activities activities = WorkflowSupport.activities();
        Activities narrate = WorkflowSupport.llmActivities();

        Activities.EntanglementResult entanglement = activities.rollEntanglement(
            request.wantedLevel(),
            request.currentHeat()
        );
        narrate.narrate(
            "Entanglement",
            entanglement.name() + ": " + entanglement.description()
        );

        while (!closed) {
            Workflow.await(() -> closed || !pending.isEmpty());
            while (!pending.isEmpty()) {
                resolveActivity(pending.poll(), activities, narrate);
            }
        }

        return new DowntimeResult("Downtime complete. Entanglement: " + entanglement.name());
    }

    private void resolveActivity(
        DowntimeActivityChoice choice,
        Activities activities,
        Activities narrate
    ) {
        String pcWorkflowId = resolvePcWorkflowId(choice.pcId());
        PlayerWorkflow pc = pcWorkflowId == null
            ? null
            : Workflow.newExternalWorkflowStub(PlayerWorkflow.class, pcWorkflowId);
        CampaignWorkflow campaign = blank(request.campaignWorkflowId())
            ? null
            : Workflow.newExternalWorkflowStub(CampaignWorkflow.class, request.campaignWorkflowId());

        if (choice.extraPaidWithCoin() && campaign != null) {
            campaign.adjustCoin(-1);
        }

        switch (choice.kind()) {
            case ACQUIRE_ASSET -> {
                Activities.AcquireAssetResult asset = activities.acquireAsset(
                    request.crewTier(),
                    choice.details() == null ? "asset" : choice.details()
                );
                narrate.narrate("Acquire asset", asset.notes());
                if (pc != null) {
                    String label = (choice.details() == null ? "asset" : choice.details())
                        + " (q" + asset.quality() + ")";
                    pc.addPersonalAsset(label);
                }
            }
            case LONG_TERM_PROJECT -> {
                if (pc != null) {
                    String name = choice.details() == null ? "project" : choice.details();
                    // Tick existing project; if none, start an 8-segment clock then tick.
                    pc.startProject(name, 8);
                    pc.applyProjectProgress(name, 1);
                }
            }
            case RECOVER -> {
                Activities.RecoveryRollResult recovery =
                    activities.recover(Math.max(1, request.crewTier() + 1));
                narrate.narrate("Recover", recovery.notes());
                if (pc != null && recovery.segments() > 0) {
                    pc.applyRecovery(recovery.segments(), Harm.RecoveryChoice.REDUCE_ALL);
                }
            }
            case REDUCE_HEAT -> {
                DiceRoll roll = activities.reduceHeat(Math.max(1, request.crewTier() + 1));
                int cleared = roll.highest();
                narrate.narrate("Reduce heat", "Cleared " + cleared + " heat.");
                if (campaign != null && cleared > 0) {
                    campaign.adjustHeat(-cleared);
                }
            }
            case TRAIN -> {
                if (pc != null) {
                    pc.markXp(parseTrainTrack(choice.details()), 1);
                }
            }
            case INDULGE_VICE -> {
                DiceRoll vice = activities.rollVice(parseViceDice(choice.details()));
                narrate.narrate("Indulge vice", "Clears up to " + vice.highest() + " stress.");
                if (pc != null) {
                    pc.resolveVice(vice);
                }
            }
        }
    }

    private Advancement.XpTrack parseTrainTrack(String details) {
        if (details == null) {
            return Advancement.XpTrack.PLAYBOOK;
        }
        String d = details.trim().toUpperCase();
        if (d.contains("INSIGHT")) {
            return Advancement.XpTrack.INSIGHT;
        }
        if (d.contains("PROWESS")) {
            return Advancement.XpTrack.PROWESS;
        }
        if (d.contains("RESOLVE")) {
            return Advancement.XpTrack.RESOLVE;
        }
        return Advancement.XpTrack.PLAYBOOK;
    }

    private int parseViceDice(String details) {
        if (details == null || details.isBlank()) {
            return 1;
        }
        try {
            return Integer.parseInt(details.trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private String resolvePcWorkflowId(String pcId) {
        for (String id : request.pcWorkflowIds()) {
            if (id != null && (id.equals(pcId) || id.endsWith("-" + pcId))) {
                return id;
            }
        }
        if (!blank(request.campaignWorkflowId())) {
            return WorkflowSupport.pcWorkflowId(request.campaignWorkflowId(), pcId);
        }
        return null;
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }

    @Override
    public void chooseActivity(String pcId, DowntimeActivityChoice choice) {
        if (closed || request == null) {
            return;
        }
        if (!request.pcIds().contains(pcId) && !request.pcIds().contains(choice.pcId())) {
            return;
        }
        String id = choice.pcId() != null ? choice.pcId() : pcId;
        int used = freeSlotsUsed.getOrDefault(id, 0);
        if (used >= request.freeActivitiesPerPc() && !choice.extraPaidWithCoin()) {
            return;
        }
        if (used < request.freeActivitiesPerPc()) {
            freeSlotsUsed.put(id, used + 1);
        }
        submitted.computeIfAbsent(id, k -> new ArrayList<>()).add(choice);
        pending.add(choice);
    }

    @Override
    public void closeDowntime() {
        closed = true;
    }

    @Override
    public Map<String, List<DowntimeActivityChoice>> getSubmittedActivities() {
        Map<String, List<DowntimeActivityChoice>> copy = new HashMap<>();
        for (Map.Entry<String, List<DowntimeActivityChoice>> e : submitted.entrySet()) {
            copy.put(e.getKey(), List.copyOf(e.getValue()));
        }
        return Map.copyOf(copy);
    }
}
