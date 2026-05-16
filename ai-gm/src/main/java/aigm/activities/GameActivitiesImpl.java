package aigm.activities;

import aigm.gamestate.enums.Action;
import aigm.gamestate.GameState;
import aigm.gamestate.Player;
import aigm.gamestate.TurnResult;

public class GameActivitiesImpl implements GameActivities {

    //TODO: Split Handle Turn into multiple basic activites:
    // - Get Player Input
    // - Handle Action (with separate handlers for each action)
    // - Handle Naration
    // - Roll Dice

    @Override
    public TurnResult handleTurn(GameState state) {

        String action = getPlayerInput();

        String narration = handleAction(action, state);

        return new TurnResult(state, narration);
    }

    private String getPlayerInput() {
        System.out.print("> ");

        java.util.Scanner scanner = new java.util.Scanner(System.in);

        String input;
        String action = null;

        while (action == null) {
            System.out.println("I'm not sure what action you're taking. What are you trying to do?");
            System.out.print("> ");

            input = scanner.nextLine();
            action = extractAction(input);
        }

        scanner.close();

        return action;
    }

    private String extractAction(String input) {
        String lower = input.toLowerCase();

        for (Action action : Action.values()) {
            if (lower.contains(action.toString().toLowerCase())) {
                return action.toString().toLowerCase();
            }
        }

        // basic synonyms
        if (lower.contains("sneak")) return "prowl";
        if (lower.contains("attack") || lower.contains("fight")) return "skirmish";
        if (lower.contains("persuade") || lower.contains("convince")) return "sway";

        return null;
    }

    private String handleAction(String action, GameState state) {
        handleStress(state);

        return "You " + action + ".";
    }

    private void handleStress(GameState state) {
        int stress = state.players().stream().filter(
            p -> p.getName().equals("player")
        ).findFirst().orElse(new Player("player")).getStress();
        state.changeStress("player", stress + 1);
    }

    // private String handleRecover(GameState state) {
    //     int stress = state.players().stream().filter(
    //     p -> p.getName().equals("player")
    // ).findFirst().orElse(new Player("player")).getStress();
    //     int newStress = Math.max(0, stress - 2);

    //     state.changeStress("player", newStress);

    //     return "You take a moment to recover. (Stress -2)";
    // }
}