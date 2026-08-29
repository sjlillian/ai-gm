package aigm.client.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import aigm.gamestate.json.GameDataConverter;

class WebResourcesTest {

    @Test
    void packagesTheStaticUi() {
        assertNotNull(GameWebServer.class.getResource("/web/index.html"));
        assertNotNull(GameWebServer.class.getResource("/web/app.js"));
        assertNotNull(GameWebServer.class.getResource("/web/styles.css"));
    }

    @Test
    void serializesADetachedView() throws Exception {
        String json = GameDataConverter.mapper().writeValueAsString(UiView.detached());
        assertTrue(json.contains("\"attached\":false"));
    }
}
