package aigm;

import aigm.client.cli.GameCli;

/**
 * Default entrypoint: interactive CLI over {@link aigm.client.TemporalGameClient}.
 * Worker must already be running ({@link aigm.workers.GameWorker}).
 */
public class App {
    public static void main(String[] args) {
        GameCli.main(args);
    }
}
