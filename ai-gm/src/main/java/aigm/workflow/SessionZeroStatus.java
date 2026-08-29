package aigm.workflow;

import java.util.List;

/** Read model for Session 0 progress on the campaign. */
public record SessionZeroStatus(
    CrewCreationStep crewStep,
    boolean joiningClosed,
    List<String> joinedPcIds,
    List<String> readyPcIds,
    int extraUpgradesPicked
) {
    public SessionZeroStatus {
        crewStep = crewStep == null ? CrewCreationStep.WAITING_FOR_JOIN : crewStep;
        joinedPcIds = joinedPcIds == null ? List.of() : List.copyOf(joinedPcIds);
        readyPcIds = readyPcIds == null ? List.of() : List.copyOf(readyPcIds);
    }
}
