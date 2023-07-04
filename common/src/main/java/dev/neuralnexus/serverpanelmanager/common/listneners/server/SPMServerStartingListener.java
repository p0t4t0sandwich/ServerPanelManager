package dev.neuralnexus.serverpanelmanager.common.listneners.server;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Listens for server starts and sends them to the message relay.
 */
public interface SPMServerStartingListener {
    /**
     * Called when a server is starting.
     */
    static void onServerStarting() {
        runTaskAsync(() -> {
            try {
                // TODO: Add trigger/task system
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
