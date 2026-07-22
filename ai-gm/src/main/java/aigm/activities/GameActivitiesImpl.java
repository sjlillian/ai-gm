package aigm.activities;

import aigm.gamestate.enums.Action;
import aigm.gamestate.Player;

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
            if (lower.contains(action.toString().toLowerCase())) { // TODO: Add synonyms to ACTIONS enum
                return action;
            }
        }
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


    // Can be used for any roll that uses a players action rating to determine the amount of dice to roll
    private void actionRoll(Player player, Action action) {
        int diceAmount = player.getActionRating(action);
        int roll = rollDice(diceAmount);
    }

    // Can be used for any roll that uses a players attribute rating to determine the amount of dice to roll
    private void resistanceRoll(Player player, Action action) {
        int diceAmount = action.getAttribute().calculateRating(player);
        int roll = rollDice(diceAmount);
    }

    private int rollDice(int diceAmount) {
        Random random = new Random();
        int max = 0;
        for (int i = 0; i < diceAmount; i++) {
            int roll = random.nextInt() + 1;
            if (roll > max)
                max = roll;
        }
        return roll;
    }


}