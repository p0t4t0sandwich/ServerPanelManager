package dev.neuralnexus.serverpanelmanager.bungee.listeners.player;

import dev.neuralnexus.serverpanelmanager.bungee.player.BungeePlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLoginListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLogoutListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerMessageListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerServerSwitchListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Listens for player events.
 */
public class BungeePlayerListener implements Listener {
    /**
     * Called when a player logs in.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerLogin(ServerSwitchEvent event) {
        // If player is switching servers, don't run this function
        if (event.getFrom() != null) return;

        // Get Player and current server
        ProxiedPlayer player = event.getPlayer();
        String toServer = event.getPlayer().getServer().getInfo().getName();

        BungeePlayer abstractPlayer = new BungeePlayer(player);
        abstractPlayer.setServerName(toServer);

        // Pass player to helper function
        SPMPlayerLoginListener.onPlayerLogin(abstractPlayer);
    }

    /**
     * Called when a player logs out.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerLogout(PlayerDisconnectEvent event) {
        // Pass player to helper function
        SPMPlayerLogoutListener.onPlayerLogout(new BungeePlayer(event.getPlayer()));
    }

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
        SPMPlayerMessageListener.onPlayerMessage(new BungeePlayer(player), message, event.isCancelled());
    }

    /**
     * Called when a player switches servers.
     * @param event The event.
     */
    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        // If player is just joining, don't run this function
        if (event.getFrom() == null) return;

        // Get Player and current server
        ProxiedPlayer player = event.getPlayer();
        String toServer = player.getServer().getInfo().getName();

        // Pass Player and current server to helper function
        SPMPlayerServerSwitchListener.onServerSwitch(new BungeePlayer(player), toServer);
    }
}
