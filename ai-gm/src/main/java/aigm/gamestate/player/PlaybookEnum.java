package aigm.gamestate.player;

import java.util.List;
import java.util.Map;

import aigm.gamestate.Ability;

public enum PlaybookEnum implements Playbook {
    CUTTER("Cutter", Map.of(), List.of(), List.of(), List.of()),
    HOUND("Hound", Map.of(), List.of(), List.of(), List.of()),
    LEECH("Leech", Map.of(), List.of(), List.of(), List.of()),
    LURK("Lurk", Map.of(), List.of(), List.of(), List.of()),
    SLIDE("Slide", Map.of(), List.of(), List.of(), List.of()),
    SPIDER("Spider", Map.of(), List.of(), List.of(), List.of()),
    WHISPER("Whisper", Map.of(), List.of(), List.of(), List.of()),
    CUSTOM("Custom", Map.of(), List.of(), List.of(), List.of());

    private final String name;
    private final Map<Action, Integer> startingActionRatings;
    private final List<Ability> availableAbilities;
    private final List<Item> availableItems;
    private final List<String> xpTriggers;

    PlaybookEnum(String name, Map<Action, Integer> startingActionRatings, List<Ability> availableAbilities, List<Item> availableItems, List<String> xpTriggers) {
        this.name = name;
        this.startingActionRatings = startingActionRatings;
        this.availableAbilities = availableAbilities;
        this.availableItems = availableItems;
        this.xpTriggers = xpTriggers;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<Action, Integer> getStartingActionRatings() {
        return startingActionRatings;
    }

    @Override
    public List<Ability> getAvailableAbilities() {
        return availableAbilities;
    }

    @Override
    public List<Item> getAvailableItems() {
        return availableItems;
    }

    @Override
    public List<String> getXpTriggers() {
        return xpTriggers;
    }   

}
