package dev.neuralnexus.serverpanelmanager.bukkit.listeners.paper.server;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartedListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PaperServerListener implements Listener {
    @EventHandler
    public void onServerStarted(ServerTickStartEvent event) {
        // Run server started helper function
        SPMServerStartedListener.onServerStarted();
        // Unregister this listener
        event.getHandlers().unregister(this);
    }
}
