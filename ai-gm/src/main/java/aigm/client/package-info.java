/**
 * UI-agnostic game client API.
 * <p>
 * Temporal workflows own durable state. This package is the only surface Discord,
 * web, desktop, or CLI UIs should call — never Temporal stubs directly.
 * {@link aigm.client.temporal.TemporalGameClient} is the production backend;
 * {@link aigm.client.cli} is one thin adapter over {@link aigm.client.GameClient}.
 */
package aigm.client;
