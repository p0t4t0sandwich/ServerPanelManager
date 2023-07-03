package dev.neuralnexus.serverpanelmanager.forge.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartingListener;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for server starting.
 */
public class ForgeServerStartingListener implements SPMServerStartingListener {
    /**
     * Called when the server is starting.
     * @param event The server starting event
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Pass server starting to helper function
        SPMServerStarting();
    }
}
