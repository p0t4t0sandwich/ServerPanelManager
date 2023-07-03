package dev.neuralnexus.serverpanelmanager.bukkit.listeners.player;

import dev.neuralnexus.serverpanelmanager.bukkit.player.BukkitPlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLogoutListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for player logouts.
 */
public class BukkitPlayerLogoutListener implements Listener, SPMPlayerLogoutListener {
    /**
     * Called when a player logs out.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        // Pass player to helper function
        SPMPlayerLogout(new BukkitPlayer(event.getPlayer()));
    }
}
