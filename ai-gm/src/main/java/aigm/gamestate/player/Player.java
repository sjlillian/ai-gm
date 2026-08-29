package aigm.gamestate.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import aigm.gamestate.Ability;
import aigm.gamestate.Contact;

public record Player(
    String name,
    Heritage heritage,
    Background background,
    Vice vice,
    Trauma trauma,
    Map<Action, Integer> actionRatings,
    Harm harm,
    int coin,
    int stash,
    Playbook playbook,
    List<Ability> abilities,
    Advancement advancement,
    Loadout loadout,
    Contact friend,
    Contact rival,
    String alias,
    String look,
    String heritageDetail,
    String backgroundDetail
) {

    public static final int COIN_CAP = 4;
    public static final int ACTION_RATING_CAP = 4;
    public static final int CREATION_ACTION_RATING_CAP = 2;
    public static final int CREATION_EXTRA_DOTS = 4;

    public Player {
        actionRatings = actionRatings == null ? Map.of() : Map.copyOf(actionRatings);
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
        coin = Math.max(0, Math.min(coin, COIN_CAP));
        stash = Math.max(0, stash);
        name = name == null ? "" : name;
        alias = alias == null ? "" : alias;
        look = look == null ? "" : look;
        heritageDetail = heritageDetail == null ? "" : heritageDetail;
        backgroundDetail = backgroundDetail == null ? "" : backgroundDetail;
    }

    /** Incomplete sheet used until Session 0 / replacement character creation finishes. */
    public static Player draft(String joinId) {
        if (joinId == null || joinId.isBlank()) {
            throw new IllegalArgumentException("joinId required");
        }
        return new Player(
            joinId,
            null,
            null,
            null,
            new Trauma(),
            Map.of(),
            new Harm(),
            0,
            0,
            null,
            List.of(),
            new Advancement(),
            new Loadout(),
            null,
            null,
            "",
            "",
            "",
            ""
        );
    }

    public int getActionRating(Action action) {
        return actionRatings.getOrDefault(action, 0);
    }

    /**
     * Dots added on top of the playbook's printed ratings. Zero if no playbook yet.
     */
    @JsonIgnore
    public int extraActionDots() {
        if (playbook == null) {
            return 0;
        }
        int extra = 0;
        Map<Action, Integer> starting = playbook.getStartingActionRatings();
        for (Action action : Action.values()) {
            int current = actionRatings.getOrDefault(action, 0);
            int base = starting.getOrDefault(action, 0);
            extra += Math.max(0, current - base);
        }
        return extra;
    }

    @JsonIgnore
    public boolean isCreationComplete() {
        return playbook != null
            && heritage != null
            && !heritageDetail.isBlank()
            && background != null
            && !backgroundDetail.isBlank()
            && extraActionDots() >= CREATION_EXTRA_DOTS
            && !abilities.isEmpty()
            && friend != null
            && rival != null
            && vice != null
            && vice.kind() != null
            && vice.purveyor() != null
            && !vice.purveyor().isBlank()
            && !name.isBlank()
            && !alias.isBlank()
            && !look.isBlank();
    }

    public Player withName(String name) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withTrauma(Trauma trauma) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withActionRatings(Map<Action, Integer> actionRatings) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withActionRating(Action action, int newRating) {
        int capped = Math.max(0, Math.min(newRating, ACTION_RATING_CAP));
        Map<Action, Integer> updated = new HashMap<>(actionRatings);
        updated.put(action, capped);
        return withActionRatings(updated);
    }

    public Player withHarm(Harm harm) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withCoin(int coin) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withStash(int stash) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withPlaybook(Playbook playbook) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withVice(Vice vice) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withAbilities(List<Ability> abilities) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withAdvancement(Advancement advancement) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withLoadout(Loadout loadout) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withFriend(Contact friend) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withRival(Contact rival) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    public Player withHeritage(Heritage heritage, String detail) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, detail, backgroundDetail);
    }

    public Player withBackground(Background background, String detail) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, detail);
    }

    public Player withIdentity(String name, String alias, String look) {
        return copy(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }

    private static Player copy(
        String name,
        Heritage heritage,
        Background background,
        Vice vice,
        Trauma trauma,
        Map<Action, Integer> actionRatings,
        Harm harm,
        int coin,
        int stash,
        Playbook playbook,
        List<Ability> abilities,
        Advancement advancement,
        Loadout loadout,
        Contact friend,
        Contact rival,
        String alias,
        String look,
        String heritageDetail,
        String backgroundDetail
    ) {
        return new Player(name, heritage, background, vice, trauma, actionRatings, harm, coin, stash, playbook, abilities, advancement, loadout, friend, rival, alias, look, heritageDetail, backgroundDetail);
    }
}
