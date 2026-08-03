package aigm.gamestate.player;

import java.util.HashMap;
import java.util.Map;

public record Player (
    String name,
    Trauma trauma,
    Map<Action, Integer> actionRatings,
    Harm harm,
    int coin,
    int stash,
    Playbook playbook 
    ) {

    public Player withName(String name) {
        return new Player(name, trauma, actionRatings, harm, coin, stash, playbook);
    }

    public Player withTrauma(Trauma trauma) {
        return new Player(name, trauma, actionRatings, harm, coin, stash, playbook);
    }

    public Player withActionRatings(Map<Action, Integer> actionRatings) {
        return new Player(name, trauma, actionRatings, harm, coin, stash, playbook);
    }

    public Player withActionRating(Action action, int newRating) {
        Map<Action, Integer> updated = new HashMap<>(actionRatings);
        updated.put(action, newRating);
        return new Player(name, trauma, updated, harm, coin, stash, playbook);
    }

    public Player withHarm(Harm harm) {
        return new Player(name, trauma, actionRatings, harm, coin, stash, playbook);
    }

    public Player withCoin(int coin) {
        return new Player(name, trauma, actionRatings, harm, coin, stash, playbook);
    }

    public Player withStash(int stash) {
        return new Player(name, trauma, actionRatings, harm, coin, stash, playbook);
    }

    public Player withPlaybook(Playbook playbook) {
        return new Player(name, trauma, actionRatings, harm, coin, stash, playbook);
    }
    
    
}
