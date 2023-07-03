package dev.neuralnexus.serverpanelmanager.bungee.listeners.player;

import dev.neuralnexus.serverpanelmanager.bungee.player.BungeePlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerMessageListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Listens for player messages.
 */
public class BungeePlayerMessageListener implements Listener, SPMPlayerMessageListener {
    /**
     * Called when a player sends a message.
     * @param event The event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMessage(ChatEvent event) {
        // If the message is a command, ignore
        if (event.isCommand() || event.isProxyCommand()) return;

        // Get player, message and server
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();

        // Pass message to helper function
        SPMPlayerMessage(new BungeePlayer(player), message, event.isCancelled());
    }
}
