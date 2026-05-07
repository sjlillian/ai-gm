package aigm.activities;

import aigm.gamestate.enums.Action;
import aigm.gamestate.GameState;
import aigm.gamestate.TurnResult;

public class GameActivitiesImpl implements GameActivities {

    @Override
    public TurnResult handleTurn(String input, GameState state) {

        String action = extractAction(input);

        java.util.Scanner scanner = new java.util.Scanner(System.in);

        // --- Clarification loop ---
        while (action == null) {
            System.out.println("I'm not sure what action you're taking. What are you trying to do?");
            System.out.print("> ");

            input = scanner.nextLine();
            action = extractAction(input);
        }

        // --- Resolve action ---
        switch (action) {
            case "hunt":
                return new TurnResult(state, handleHunt(state));
            case "study":
                return new TurnResult(state, handleStudy(state));
            case "survey":
                return new TurnResult(state, handleSurvey(state));
            case "tinker":
                return new TurnResult(state, handleTinker(state));
            case "finesse":
                return new TurnResult(state, handleFinesse(state));
            case "prowl":
                return new TurnResult(state, handleProwl(state));
            case "skirmish":
                return new TurnResult(state, handleSkirmish(state));
            case "wreck":
                return new TurnResult(state, handleWreck(state));
            case "attune":
                return new TurnResult(state, handleAttune(state));
            case "command":
                return new TurnResult(state, handleCommand(state));
            case "consort":
                return new TurnResult(state, handleConsort(state));
            case "sway":
                return new TurnResult(state, handleSway(state));

            default:
                return new TurnResult(state, "You act, but nothing significant happens.");
        }
    }

    @Override
    public String getPlayerInput() {
        System.out.print("> ");

        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        return scanner.nextLine();
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

    private String handleHunt(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You go on the hunt. (Stress +1)";
    }

    private String handleStudy(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You study your surroundings. (Stress +1)";
    }

    private String handleSurvey(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You survey the area. (Stress +1)";
    }

    private String handleTinker(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You tinker with your gear. (Stress +1)";
    }

    private String handleFinesse(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You perform a delicate action. (Stress +1)";
    }


    private String handleProwl(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You melt into the shadows. (Stress +1)";
    }

    private String handleSkirmish(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 2);

        return "You engage in brutal close combat. (Stress +2)";
    }

    private String handleWreck(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 2);

        return "You unleash a powerful attack. (Stress +2)";
    }

    private String handleAttune(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You attune to your surroundings. (Stress +1)";
    }

    private String handleCommand(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You command an ally to act. (Stress +1)";
    }

    private String handleConsort(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You consort with someone to gain information. (Stress +1)";
    }

    private String handleSway(GameState state) {
        int stress = state.playerStress().get("player");
        state.setStress("player", stress + 1);

        return "You attempt to sway someone to your side. (Stress +1)";
    }

    // private String handleRecover(GameState state) {
    //     int stress = state.playerStress().get("player");
    //     int newStress = Math.max(0, stress - 2);

    //     state.setStress("player", newStress);

    //     return "You take a moment to recover. (Stress -2)";
    // }
}