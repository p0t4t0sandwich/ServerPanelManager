package dev.neuralnexus.serverpanelmanager.forge.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStoppedListener;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for server stopped and sends it to the message relay.
 */
public class ForgeServerStoppedListener implements SPMServerStoppedListener {
    /**
     * Called when the server stops, and sends it to the message relay.
     * @param event The server stopped event
     */
    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        // Pass server stopped to helper function
        SPMServerStopped();
    }
}
