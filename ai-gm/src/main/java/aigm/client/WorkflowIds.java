package aigm.client;

/** Deterministic Temporal workflow IDs. Keep in sync with campaign child-start code. */
public final class WorkflowIds {

    private WorkflowIds() {}

    public static String score(String campaignWorkflowId, int cycleNumber) {
        return "score-" + campaignWorkflowId + "-" + cycleNumber;
    }

    public static String downtime(String campaignWorkflowId, int cycleNumber) {
        return "downtime-" + campaignWorkflowId + "-" + cycleNumber;
    }

    public static String pc(String campaignWorkflowId, String pcName) {
        return "pc-" + campaignWorkflowId + "-" + pcName;
    }
}
