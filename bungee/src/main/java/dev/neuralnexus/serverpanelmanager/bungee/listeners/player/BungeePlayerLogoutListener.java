package dev.neuralnexus.serverpanelmanager.bungee.listeners.player;

import dev.neuralnexus.serverpanelmanager.bungee.player.BungeePlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLogoutListener;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Listens for player logouts.
 */
public class BungeePlayerLogoutListener implements Listener, SPMPlayerLogoutListener {
    /**
     * Called when a player logs out.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerLogout(PlayerDisconnectEvent event) {
        // Pass player to helper function
        SPMPlayerLogout(new BungeePlayer(event.getPlayer()));
    }
}
