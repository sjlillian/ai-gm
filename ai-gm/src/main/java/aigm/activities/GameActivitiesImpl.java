package aigm.activities;

import aigm.gamestate.GameState;

public class GameActivitiesImpl implements GameActivities {

    @Override
    public String handleTurn(String input, GameState state) {

        String lower = input.toLowerCase();

        // --- Basic Intent Detection ---
        if (lower.contains("sneak") || lower.contains("prowl")) {
            return handleProwl(state);
        }

        if (lower.contains("attack") || lower.contains("fight")) {
            return handleSkirmish(state);
        }
        

        if (lower.contains("rest") || lower.contains("recover")) {
            return handleRecover(state);
        }

        // Default fallback
        return "You hesitate, unsure how to act.";
    }

    @Override
    public String getPlayerInput() {
        System.out.print("> ");

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        return scanner.nextLine();
    }


    private String handleProwl(GameState state) {
        int stress = state.getStress("player");
        state.setStress("player", stress + 1);

        return "You melt into the shadows. (Stress +1)";
    }

    private String handleSkirmish(GameState state) {
        int stress = state.getStress("player");
        state.setStress("player", stress + 2);

        return "You engage in brutal close combat. (Stress +2)";
    }

    private String handleRecover(GameState state) {
        int stress = state.getStress("player");
        int newStress = Math.max(0, stress - 2);

        state.setStress("player", newStress);

        return "You take a moment to recover. (Stress -2)";
    }
}