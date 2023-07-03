package dev.neuralnexus.serverpanelmanager.bukkit.listeners.player;

import dev.neuralnexus.serverpanelmanager.bukkit.player.BukkitPlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLoginListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listens for player logins.
 */
public class BukkitPlayerLoginListener implements Listener, SPMPlayerLoginListener {
    /**
     * Called when a player logs in.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        // Pass player to helper function
        SPMPlayerLogin(new BukkitPlayer(event.getPlayer()));
    }
}
