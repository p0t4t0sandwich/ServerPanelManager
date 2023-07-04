package dev.neuralnexus.serverpanelmanager.bukkit.listeners.player;

import dev.neuralnexus.serverpanelmanager.bukkit.player.BukkitPlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for player events.
 */
public class BukkitPlayerListener implements Listener {
    /**
     * Called when a player completes an advancement.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceChat()) {
            // Pass advancement to helper function
            SPMPlayerAdvancementListener.onPlayerAdvancement(new BukkitPlayer(event.getPlayer()), advancement.getDisplay().getTitle());
        }
    }

    /**
     * Called when a player dies.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Pass death to helper function
        SPMPlayerDeathListener.onPlayerDeath(new BukkitPlayer(event.getEntity()), event.getDeathMessage());
    }

    /**
     * Called when a player logs in.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        // Pass player to helper function
        SPMPlayerLoginListener.onPlayerLogin(new BukkitPlayer(event.getPlayer()));
    }

    /**
     * Called when a player logs out.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        // Pass player to helper function
        SPMPlayerLogoutListener.onPlayerLogout(new BukkitPlayer(event.getPlayer()));
    }

    /**
     * Called when a player sends a message.
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
        // Pass message to helper function
        SPMPlayerMessageListener.onPlayerMessage(new BukkitPlayer(event.getPlayer()), event.getMessage(), event.isCancelled());
    }
}
