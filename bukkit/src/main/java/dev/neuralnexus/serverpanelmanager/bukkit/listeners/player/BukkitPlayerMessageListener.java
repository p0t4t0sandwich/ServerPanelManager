package dev.neuralnexus.serverpanelmanager.bukkit.listeners.player;

import dev.neuralnexus.serverpanelmanager.bukkit.player.BukkitPlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerMessageListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Listens for player messages.
 */
public class BukkitPlayerMessageListener implements Listener, SPMPlayerMessageListener {
    /**
     * Called when a player sends a message.
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
        // Pass message to helper function
        SPMPlayerMessage(new BukkitPlayer(event.getPlayer()), event.getMessage(), event.isCancelled());
    }
}
