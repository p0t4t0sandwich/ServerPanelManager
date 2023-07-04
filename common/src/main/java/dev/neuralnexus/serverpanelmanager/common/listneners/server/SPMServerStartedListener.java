package dev.neuralnexus.serverpanelmanager.common.listneners.server;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Listens for server starts.
 */
public interface SPMServerStartedListener {
    /**
     * Called when a server is started.
     */
    static void onServerStarted() {
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
