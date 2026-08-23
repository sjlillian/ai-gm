package aigm.llm;

/**
 * Deterministic stand-in used when no model is configured. Lets the worker
 * boot and workflows run without a local or cloud LLM.
 */
public final class StubLlmClient implements LlmClient {

    public static final String MODEL_ID = "stub";

    @Override
    public LlmResponse complete(LlmRequest request) {
        String content = request.jsonObject()
            ? """
              {"action":"PROWL","position":"RISKY","effect":"STANDARD",\
              "reasoning":"Stub GM: no model configured. Risky / standard is the default action-roll framing.",\
              "possibleStakes":["Complication","Harm","Clock advances","Worse position"]}
              """
            : "The lamps of Doskvol throw long shadows. (Stub GM — set AIGM_LLM_BASE_URL "
                + "or AIGM_LLM_API_KEY to enable a model.)";
        return new LlmResponse(content, MODEL_ID, LlmResponse.Usage.NONE, "stop");
    }

    @Override
    public String describe() {
        return "StubLlmClient (no remote model)";
    }
}
