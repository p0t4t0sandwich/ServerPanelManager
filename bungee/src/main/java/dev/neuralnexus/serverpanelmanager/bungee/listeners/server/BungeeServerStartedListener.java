package dev.neuralnexus.serverpanelmanager.bungee.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartedListener;

/**
 * Listens for server startup.
 */
public class BungeeServerStartedListener implements SPMServerStartedListener {
    /**
     * Called when the server is started.
     */
    public void onServerStarted() {
        // Pass server started to helper function
        SPMServerStarted();
    }
}
