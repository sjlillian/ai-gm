package aigm.client.web;

import aigm.client.TemporalGameClient;

/**
 * Browser UI over {@link TemporalGameClient}. Worker must already be running.
 */
public final class GameUi {

    private GameUi() {}

    public static void main(String[] args) throws Exception {
        int port = port();
        try (TemporalGameClient client = new TemporalGameClient();
             GameWebServer server = new GameWebServer(client, port)) {
            server.start();
            System.out.println("AI-GM UI http://localhost:" + server.port());
            System.out.println("Worker must already be running.");
            Thread.currentThread().join();
        }
    }

    private static int port() {
        String raw = System.getenv("AIGM_UI_PORT");
        if (raw == null || raw.isBlank()) {
            return 8080;
        }
        return Integer.parseInt(raw.trim());
    }
}
