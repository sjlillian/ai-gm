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
                "factions":[{"faction":"The Lampblacks","status":"HELPFUL"},{"faction":"The Crows","status":"HOSTILE"}]}
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
