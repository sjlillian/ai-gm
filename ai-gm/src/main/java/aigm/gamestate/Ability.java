package aigm.gamestate;

public interface Ability {

    public enum Scope {
        PLAYER, CREW;
    }

    String getName();
    String getDescription();
    Scope getScope();

}
