package aigm;

import aigm.client.cli.GameCli;

/**
 * Default entrypoint: interactive CLI over the UI-agnostic {@link aigm.client.GameClient}.
 * Worker must already be running ({@link aigm.workers.GameWorker}).
 */
public class App {
    public static void main(String[] args) {
        GameCli.main(args);
    }
}
