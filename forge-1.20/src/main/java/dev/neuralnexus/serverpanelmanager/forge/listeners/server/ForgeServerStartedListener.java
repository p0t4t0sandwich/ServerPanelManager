package dev.neuralnexus.serverpanelmanager.forge.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartedListener;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for server started.
 */
public class ForgeServerStartedListener implements SPMServerStartedListener {
    /**
     * Called when the server is started.
     * @param event The server started event
     */
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        // Pass server started to helper function
        SPMServerStarted();
    }
}
