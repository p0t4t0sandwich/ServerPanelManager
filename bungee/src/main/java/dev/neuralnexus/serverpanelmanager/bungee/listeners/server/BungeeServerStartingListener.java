package dev.neuralnexus.serverpanelmanager.bungee.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartingListener;

/**
 * Listens for server starting.
 */
public class BungeeServerStartingListener implements SPMServerStartingListener {
    /**
     * Called when the server is starting.
     */
    public void onServerStarting() {
        // Pass server starting to helper function
        SPMServerStarting();
    }
}
