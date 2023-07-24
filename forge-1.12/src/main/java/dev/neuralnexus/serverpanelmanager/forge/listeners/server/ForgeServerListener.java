package dev.neuralnexus.serverpanelmanager.forge.listeners.server;

import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartedListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartingListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStoppedListener;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

/**
 * Listens for server events.
 */
@Mod.EventBusSubscriber(modid = "serverpanelmanager")
public class ForgeServerListener {
    /**
     * Called when the server is started.
     * @param event The server started event
     */
    @EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        // Pass server started to helper function
        SPMServerStartedListener.onServerStarted();
    }

    /**
     * Called when the server is starting.
     * @param event The server starting event
     */
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        // Pass server starting to helper function
        SPMServerStartingListener.onServerStarting();
    }

    /**
     * Called when the server stops, and sends it to the message relay.
     * @param event The server stopped event
     */
    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        // Pass server stopped to helper function
        SPMServerStoppedListener.onServerStopped();
    }
}
