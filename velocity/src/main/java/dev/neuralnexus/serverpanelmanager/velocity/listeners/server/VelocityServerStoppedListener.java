package dev.neuralnexus.serverpanelmanager.velocity.listeners.server;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStoppedListener;

/**
 * Listens for server stopped and sends it to the message relay.
 */
public class VelocityServerStoppedListener implements SPMServerStoppedListener {
    /**
     * Called when the server stops.
     * @param event The server stopped event
     */
    @Subscribe
    public void onServerStopped(ProxyShutdownEvent event) {
        // Pass server stopped to helper function
        SPMServerStopped();
    }
}
