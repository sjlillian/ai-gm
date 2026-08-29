package aigm.client.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import aigm.client.CampaignSnapshot;
import aigm.client.DemoCrews;
import aigm.client.TemporalGameClient;
import aigm.gamestate.json.GameDataConverter;
import aigm.gamestate.player.Player;
import aigm.workflow.CreationPrompt;

/**
 * Serves the static UI and a JSON API over {@link TemporalGameClient}.
 */
public final class GameWebServer implements AutoCloseable {

    private static final ObjectMapper JSON = GameDataConverter.mapper();
    private static final String STATIC_PREFIX = "/web";

    private final TemporalGameClient game;
    private final HttpServer server;
    private final Object lock = new Object();

    public GameWebServer(TemporalGameClient game, int port) throws IOException {
        this.game = game;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", this::handleApi);
        server.createContext("/", this::handleStatic);
        server.setExecutor(Executors.newCachedThreadPool());
    }

    public void start() {
        server.start();
    }

    public int port() {
        return server.getAddress().getPort();
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private void handleApi(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equals(method)) {
                send(exchange, 204, null);
                return;
            }
            if ("GET".equals(method) && path.equals("/api/view")) {
                sendJson(exchange, 200, view(query(exchange, "client")));
                return;
            }
            if ("POST".equals(method) && path.equals("/api/start")) {
                JsonNode body = readJson(exchange);
                String id = text(body, "id");
                String mode = text(body, "mode");
                String started;
                synchronized (lock) {
                    if ("demo".equalsIgnoreCase(mode)) {
                        started = game.startCampaign(DemoCrews.nightspires(), blankTo(id, "campaign-demo"));
                    } else {
                        started = game.startBlankCampaign(blankTo(id, "campaign-demo"));
                    }
                }
                sendJson(exchange, 200, Map.of("id", started, "view", view(query(exchange, "client"))));
                return;
            }
            if ("POST".equals(method) && path.equals("/api/attach")) {
                JsonNode body = readJson(exchange);
                String id = text(body, "id");
                synchronized (lock) {
                    game.attach(id);
                }
                sendJson(exchange, 200, Map.of("id", id, "view", view(query(exchange, "client"))));
                return;
            }
            if ("POST".equals(method) && path.equals("/api/respond")) {
                JsonNode body = readJson(exchange);
                String clientId = text(body, "clientId");
                CreationPrompt prompt;
                Map<String, String> fields = new LinkedHashMap<>();
                if (body.has("fields") && body.get("fields").isObject()) {
                    body.get("fields").fields().forEachRemaining(entry ->
                        fields.put(entry.getKey(), entry.getValue().asText("")));
                }
                synchronized (lock) {
                    prompt = UiCommands.respond(
                        game,
                        clientId,
                        text(body, "token"),
                        text(body, "rest"),
                        fields
                    );
                }
                sendJson(exchange, 200, Map.of("prompt", prompt, "view", view(clientId)));
                return;
            }
            if ("POST".equals(method) && path.equals("/api/action")) {
                JsonNode body = readJson(exchange);
                String actionId = text(body, "id");
                String clientId = text(body, "clientId");
                Map<String, Object> fields = Map.of();
                if (body.has("fields") && body.get("fields").isObject()) {
                    fields = JSON.convertValue(body.get("fields"), new TypeReference<Map<String, Object>>() {});
                }
                Object result;
                synchronized (lock) {
                    result = UiCommands.execute(game, actionId, clientId, fields);
                }
                Map<String, Object> payload = new LinkedHashMap<>();
                payload.put("result", result);
                payload.put("view", view(clientId));
                sendJson(exchange, 200, payload);
                return;
            }
            sendJson(exchange, 404, Map.of("error", "not found"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendJson(exchange, 400, Map.of("error", rootMessage(e)));
        } catch (RuntimeException e) {
            sendJson(exchange, 500, Map.of("error", rootMessage(e)));
        }
    }

    private void handleStatic(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod()) && !"HEAD".equals(exchange.getRequestMethod())) {
            send(exchange, 405, null);
            return;
        }
        String path = exchange.getRequestURI().getPath();
        if (path == null || path.equals("/") || path.isBlank()) {
            path = "/index.html";
        }
        if (path.contains("..") || path.contains("\\")) {
            send(exchange, 404, null);
            return;
        }
        String resource = STATIC_PREFIX + path;
        try (InputStream in = GameWebServer.class.getResourceAsStream(resource)) {
            if (in == null) {
                send(exchange, 404, null);
                return;
            }
            byte[] bytes = in.readAllBytes();
            exchange.getResponseHeaders().set("Content-Type", contentType(path));
            exchange.sendResponseHeaders(200, bytes.length);
            if (!"HEAD".equals(exchange.getRequestMethod())) {
                try (OutputStream out = exchange.getResponseBody()) {
                    out.write(bytes);
                }
            } else {
                exchange.close();
            }
        }
    }

    UiView view(String selectedClientId) {
        synchronized (lock) {
            if (!game.isAttached()) {
                return UiView.detached();
            }
            CampaignSnapshot snapshot = game.snapshot();
            Map<String, Player> sheets = new LinkedHashMap<>();
            Map<String, CreationPrompt> prompts = new LinkedHashMap<>();
            for (String joinId : UiAssembler.pcJoinIds(snapshot)) {
                try {
                    sheets.put(joinId, game.getPlayer(joinId));
                } catch (RuntimeException ignored) {
                    // Child may not be queryable immediately after join.
                }
                try {
                    prompts.put(joinId, game.getPcCreationPrompt(joinId));
                } catch (RuntimeException ignored) {
                    // same
                }
            }
            return UiAssembler.assemble(snapshot, selectedClientId, sheets, prompts);
        }
    }

    private JsonNode readJson(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        if (bytes.length == 0) {
            return JSON.createObjectNode();
        }
        return JSON.readTree(bytes);
    }

    private void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] bytes = JSON.writeValueAsBytes(body);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static void send(HttpExchange exchange, int status, byte[] body) throws IOException {
        long length = body == null ? -1 : body.length;
        exchange.sendResponseHeaders(status, length);
        if (body != null) {
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        } else {
            exchange.close();
        }
    }

    private static String query(HttpExchange exchange, String name) {
        String raw = exchange.getRequestURI().getQuery();
        if (raw == null || raw.isBlank()) {
            return null;
        }
        for (String part : raw.split("&")) {
            int eq = part.indexOf('=');
            String key = eq < 0 ? part : part.substring(0, eq);
            if (name.equals(key)) {
                return eq < 0 ? "" : java.net.URLDecoder.decode(part.substring(eq + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private static String text(JsonNode body, String field) {
        if (body == null || body.path(field).isMissingNode() || body.path(field).isNull()) {
            return "";
        }
        return body.path(field).asText("");
    }

    private static String blankTo(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static String contentType(String path) {
        if (path.endsWith(".js")) {
            return "text/javascript; charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css; charset=utf-8";
        }
        if (path.endsWith(".html")) {
            return "text/html; charset=utf-8";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream";
    }

    private static String rootMessage(Throwable error) {
        Throwable current = error;
        String last = current.getClass().getSimpleName();
        while (current != null) {
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                last = current.getMessage();
            }
            current = current.getCause();
        }
        return last;
    }
}
