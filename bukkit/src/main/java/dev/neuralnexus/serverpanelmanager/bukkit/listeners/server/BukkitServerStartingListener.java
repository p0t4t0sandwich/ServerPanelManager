package dev.neuralnexus.serverpanelmanager.bukkit.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartingListener;

/**
 * Listens for server startup.
 */
public class BukkitServerStartingListener implements SPMServerStartingListener {
    /**
     * Called when the server starts up.
     */
    public void onServerStarting() {
        // Pass server started to helper function
        SPMServerStarting();
    }
}
