package aigm.gamestate;

public interface Contact {

    public enum Scope {
        PLAYER, CREW;
    }

    String getName();
    String getDescription();
    Scope getScope();

}
