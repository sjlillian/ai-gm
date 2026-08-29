package aigm;

import aigm.client.cli.GameCli;
import aigm.client.web.GameUi;

/**
 * Default entrypoint: interactive CLI over {@link aigm.client.TemporalGameClient}.
 * Pass {@code ui} to serve the browser UI instead. Worker must already be running
 * ({@link aigm.workers.GameWorker}).
 */
public class App {
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "ui".equalsIgnoreCase(args[0])) {
            GameUi.main(args);
            return;
        }
        GameCli.main(args);
    }
}
