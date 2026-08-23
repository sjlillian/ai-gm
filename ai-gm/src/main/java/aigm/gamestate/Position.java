package aigm.gamestate;

/**
 * Action-roll position: how dangerous the attempt is (Core Rulebook p. 19–26).
 * Engagement 1–3 starts Desperate, 4–5 Risky, 6 Controlled, critical exceptional advantage.
 */
public enum Position {
    CONTROLLED("You have a golden opportunity. You're exploiting a dominant advantage."),
    RISKY("You go head to head. You're acting under duress. You're taking a chance."),
    DESPERATE("You're in serious trouble. You're overreaching. You're attempting a dangerous maneuver.");

    private final String description;

    Position(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
