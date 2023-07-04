package dev.neuralnexus.serverpanelmanager.forge.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartedListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartingListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStoppedListener;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;

/**
 * Listens for server events.
 */
public class ForgeServerListener {
    /**
     * Called when the server is started.
     * @param event The server started event
     */
    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        // Pass server started to helper function
        SPMServerStartedListener.onServerStarted();
    }

    /**
     * Called when the server is starting.
     * @param event The server starting event
     */
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // Pass server starting to helper function
        SPMServerStartingListener.onServerStarting();
    }

    /**
     * Called when the server stops, and sends it to the message relay.
     * @param event The server stopped event
     */
    @SubscribeEvent
    public void onServerStopped(FMLServerStoppedEvent event) {
        // Pass server stopped to helper function
        SPMServerStoppedListener.onServerStopped();
    }
}
