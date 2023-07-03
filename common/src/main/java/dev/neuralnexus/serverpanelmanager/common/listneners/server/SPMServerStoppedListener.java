package dev.neuralnexus.serverpanelmanager.common.listneners.server;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Listens for server starts.
 */
public interface SPMServerStoppedListener {
    /**
     * Called when a server stops.
     */
    default void SPMServerStopped() {
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
