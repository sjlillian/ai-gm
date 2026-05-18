package aigm;

import aigm.starters.GameStarter;

/**
 * This is the main TEST entrypoint to run the application. It will call the
 * Starter and make sure that a worker is running.
 */
public class App {
    public static void main(String[] args) {
        GameStarter starter = new GameStarter();
        starter.run();
    }
}
