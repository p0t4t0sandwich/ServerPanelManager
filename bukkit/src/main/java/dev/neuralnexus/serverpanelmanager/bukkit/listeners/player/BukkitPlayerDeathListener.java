package dev.neuralnexus.serverpanelmanager.bukkit.listeners.player;

import dev.neuralnexus.serverpanelmanager.bukkit.player.BukkitPlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerDeathListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Listens for player deaths.
 */
public class BukkitPlayerDeathListener implements Listener, SPMPlayerDeathListener {
    /**
     * Called when a player dies.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Pass death to helper function
        SPMPlayerDeath(new BukkitPlayer(event.getEntity()), event.getDeathMessage());
    }
}
