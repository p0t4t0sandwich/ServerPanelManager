package dev.neuralnexus.serverpanelmanager.bukkit.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartedListener;

/**
 * Listens for server startup.
 */
public class BukkitServerStartedListener implements SPMServerStartedListener {
    /**
     * Called when the server starts up.
     */
    public void onServerStarted() {
        // Pass server started to helper function
        SPMServerStarted();
    }
}
