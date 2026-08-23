package aigm.gamestate.score;

public enum ScoreType {

    ASSAULT("Do violence to a target.", "The point of attack."),
    DECEPTION("Lure, trick, or manipulate.", "The method of deception."),
    STEALTH("Trespass unseen.", "The point of infiltration."),
    OCCULT("Engage a supernatural power.", "The arcane method."),
    SOCIAL("Negotiate, bargain, or persuade.", "The social connection."),
    TRANSPORT("Carry cargo or people through danger.", "The route and means.");

    private String description;
    private String detail;

    ScoreType(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
    public String getDescription() {
        return description;
    }
    public String getDetail() {
        return detail;
    }
}
