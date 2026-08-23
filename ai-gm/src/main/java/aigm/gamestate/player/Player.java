package aigm.gamestate.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aigm.gamestate.Ability;

public record Player(
    String name,
    String heritage,
    String background,
    Vice vice,
    Trauma trauma,
    Map<Action, Integer> actionRatings,
    Harm harm,
    int coin,
    int stash,
    Playbook playbook,
    List<Ability> abilities,
    Advancement advancement,
    Loadout loadout
) {

    public static final int COIN_CAP = 4;
    public static final int ACTION_RATING_CAP = 4;

    public Player {
        actionRatings = Map.copyOf(actionRatings);
        abilities = List.copyOf(abilities);
        coin = Math.max(0, Math.min(coin, COIN_CAP));
        stash = Math.max(0, stash);
    }

    public int getActionRating(Action action) {
        return actionRatings.getOrDefault(action, 0);
    }

    public Player withName(String name) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withTrauma(Trauma trauma) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withActionRatings(Map<Action, Integer> actionRatings) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withActionRating(Action action, int newRating) {
        int capped = Math.max(0, Math.min(newRating, ACTION_RATING_CAP));
        Map<Action, Integer> updated = new HashMap<>(actionRatings);
        updated.put(action, capped);
        return withActionRatings(updated);
    }

    public Player withHarm(Harm harm) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withCoin(int coin) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withStash(int stash) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withPlaybook(Playbook playbook) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withVice(Vice vice) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withAbilities(List<Ability> abilities) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withAdvancement(Advancement advancement) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    public Player withLoadout(Loadout loadout) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }

    private static Player copy(
        String name,
        String heritage,
        String background,
        Vice vice,
        Trauma trauma,
        Map<Action, Integer> actionRatings,
        Harm harm,
        int coin,
        int stash,
        Playbook playbook,
        List<Ability> abilities,
        Advancement advancement,
        Loadout loadout
    ) {
        return new Player(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout);
    }
}
