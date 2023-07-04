package dev.neuralnexus.serverpanelmanager.forge.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartedListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartingListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStoppedListener;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for server events.
 */
public class ForgeServerListener {
    /**
     * Called when the server is started.
     * @param event The server started event
     */
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        // Pass server started to helper function
        SPMServerStartedListener.onServerStarted();
    }

    /**
     * Called when the server is starting.
     * @param event The server starting event
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Pass server starting to helper function
        SPMServerStartingListener.onServerStarting();
    }

    /**
     * Called when the server stops, and sends it to the message relay.
     * @param event The server stopped event
     */
    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        // Pass server stopped to helper function
        SPMServerStoppedListener.onServerStopped();
    }
}
