package dev.neuralnexus.serverpanelmanager.bungee.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStoppedListener;

/**
 * Listens for server shutdown.
 */
public class BungeeServerStoppedListener implements SPMServerStoppedListener {
    /**
     * Called when the server stops.
     */
    public void onServerStopped() {
        // Pass server stopped to helper function
        SPMServerStopped();
    }
}
