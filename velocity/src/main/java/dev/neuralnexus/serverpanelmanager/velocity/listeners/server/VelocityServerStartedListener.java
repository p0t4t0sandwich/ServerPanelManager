package dev.neuralnexus.serverpanelmanager.velocity.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartedListener;

/**
 * Listens for server started.
 */
public class VelocityServerStartedListener implements SPMServerStartedListener {
    /**
     * Called when the server starts.
     */
    public void onServerStarted() {
        // Pass server started to helper function
        SPMServerStarted();
    }
}
