package aigm.activities;

import aigm.gamestate.enums.Action;

public class GameActivitiesImpl implements GameActivities {

    // TODO: Split Handle Turn into multiple basic activites:
    // - Get Player Input
    // - Handle Action (with separate handlers for each action)
    // - Handle Naration
    // - Roll Dice

    public void handleTurn() {

        Action action = getPlayerInput();

        handleAction(action);

        // return new TurnResult(state, narration);
    }

    private Action getPlayerInput() {
        System.out.print("> ");

        java.util.Scanner scanner = new java.util.Scanner(System.in);

        String input;
        Action action = null;

        while (action == null) {
            System.out.println("I'm not sure what action you're taking. What are you trying to do?");
            System.out.print("> ");

            input = scanner.nextLine();
            action = extractAction(input);
        }

        scanner.close();

        return action;
    }

    private Action extractAction(String input) {
        String lower = input.toLowerCase();

        for (Action action : Action.values()) {
            if (lower.contains(action.toString().toLowerCase())) {
                return action;
            }
        }

        // basic synonyms
        if (lower.contains("sneak"))
            return Action.PROWL;
        if (lower.contains("attack") || lower.contains("fight"))
            return Action.SKIRMISH;
        if (lower.contains("persuade") || lower.contains("convince"))
            return Action.SWAY;

        return null;
    }

    public void handleAction(Action action) {
        handleStress();

        // return "You " + action + ".";
    }

    private void handleStress() {
        // int stress = state.players().stream().filter(
        // p -> p.getName().equals("player")).findFirst().orElse(new
        // Player("player")).getStress();
        // state.changeStress("player", stress + 1);
    }

    // private String handleRecover(GameState state) {
    // int stress = state.players().stream().filter(
    // p -> p.getName().equals("player")
    // ).findFirst().orElse(new Player("player")).getStress();
    // int newStress = Math.max(0, stress - 2);

    // state.changeStress("player", newStress);

    // return "You take a moment to recover. (Stress -2)";
    // }
}