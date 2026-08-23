package aigm.gamestate.campaign;

import java.util.List;

/**
 * A gang or expert in the crew's employ (Core Rulebook p. 96).
 * Gangs have a {@link CohortType}; experts use {@code expertSpecialty} instead.
 */
public record Cohort(
    String name,
    Kind kind,
    CohortType gangType,
    String expertSpecialty,
    List<CohortEdge> edges,
    List<CohortFlaw> flaws,
    int harm,
    boolean armor
) {

    public static final int HARM_BOXES = 4;

    public enum Kind {
        GANG, EXPERT
    }

    public Cohort {
        edges = List.copyOf(edges);
        flaws = List.copyOf(flaws);
        harm = Math.max(0, Math.min(harm, HARM_BOXES));
    }
}
