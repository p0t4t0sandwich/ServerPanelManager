package dev.neuralnexus.serverpanelmanager.velocity.listeners.server;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartingListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStoppedListener;

public class VelocityServerListener {
    /**
     * Called when the server starts.
     * @param event The server starting event
     */
    public void onServerStarting(ProxyInitializeEvent event) {
        // Pass server starting to helper function
        SPMServerStartingListener.onServerStarting();
    }

    /**
     * Called when the server stops.
     * @param event The server stopped event
     */
    @Subscribe
    public void onServerStopped(ProxyShutdownEvent event) {
        // Pass server stopped to helper function
        SPMServerStoppedListener.onServerStopped();
    }
}
