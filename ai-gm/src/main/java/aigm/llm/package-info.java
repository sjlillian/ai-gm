/**
 * Provider-agnostic LLM client used by GM activities.
 * <p>
 * Workflows never call a model directly. They invoke {@link aigm.llm.gm.LlmActivities},
 * which runs in a Temporal activity and delegates to {@link aigm.llm.LlmClient}.
 * That split keeps replay deterministic and lets Temporal retry timeouts and 429s.
 * <p>
 * Plug in any model — cloud or local — by:
 * <ol>
 *   <li>Pointing {@link aigm.llm.openai.OpenAiCompatibleClient} at an OpenAI-style
 *       {@code /v1/chat/completions} server (OpenAI, Groq, OpenRouter, Ollama,
 *       LM Studio, llama.cpp, vLLM, LocalAI, …) via {@code AIGM_LLM_*} env vars, or</li>
 *   <li>Implementing {@link aigm.llm.LlmClient} / {@link aigm.llm.LlmClientProvider}
 *       and selecting it with {@code AIGM_LLM_CLIENT}.</li>
 * </ol>
 * Client libraries must not retry; Temporal owns retries.
 */
package aigm.llm;
