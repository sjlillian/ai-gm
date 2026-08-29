package aigm.llm;

/**
 * Deterministic stand-in used when no model is configured. Lets the worker
 * boot and workflows run without a local or cloud LLM.
 */
public final class StubLlmClient implements LlmClient {

    public static final String MODEL_ID = "stub";

    @Override
    public LlmResponse complete(LlmRequest request) {
        String content;
        if (request.jsonObject() && requestContains(request, "Session 0")) {
            content = """
                {"fiction":"Crow's Foot is already at war. The Lampblacks want a word, and the Crows want a body. (Stub GM — set AIGM_LLM_BASE_URL or AIGM_LLM_API_KEY to enable a model.)",\
                "clocks":[{"name":"Lampblacks vs Crows","segments":6},{"name":"Bluecoat notice","segments":4}],\
                "factions":[{"faction":"The Lampblacks","status":"HELPFUL"},{"faction":"The Crows","status":"HOSTILE"}],\
                "scores":[{"title":"Steal the Crows' tribute book","hook":"Bazso Baz says the Crows keep a ledger of who pays. Lift it and Crow's Foot tilts.","targetName":"The Crows' counting house","targetTier":"ONE","planType":"STEALTH","district":"Crow's Foot"},{"title":"Sink a Lampblack boat","hook":"A smuggling skiff at the Docks is loaded with leviathan blood the Lampblacks cannot afford to lose.","targetName":"Lampblack skiff","targetTier":"ZERO","planType":"ASSAULT","district":"The Docks"},{"title":"Walk into Six Towers as mourners","hook":"A dead noble's wake is unguarded after midnight. The will in the study names enemies you could sell.","targetName":"Kellis wake","targetTier":"TWO","planType":"SOCIAL","district":"Six Towers"}]}
                """;
        } else if (request.jsonObject()) {
            content = """
              {"action":"PROWL","position":"RISKY","effect":"STANDARD",\
              "reasoning":"Stub GM: no model configured. Risky / standard is the default action-roll framing.",\
              "possibleStakes":["Complication","Harm","Clock advances","Worse position"]}
              """;
        } else {
            content = "The lamps of Doskvol throw long shadows. (Stub GM — set AIGM_LLM_BASE_URL "
                + "or AIGM_LLM_API_KEY to enable a model.)";
        }
        return new LlmResponse(content, MODEL_ID, LlmResponse.Usage.NONE, "stop");
    }

    private static boolean requestContains(LlmRequest request, String needle) {
        if (request.messages() == null) {
            return false;
        }
        for (LlmMessage message : request.messages()) {
            if (message.content() != null && message.content().contains(needle)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String describe() {
        return "StubLlmClient (no remote model)";
    }
}
