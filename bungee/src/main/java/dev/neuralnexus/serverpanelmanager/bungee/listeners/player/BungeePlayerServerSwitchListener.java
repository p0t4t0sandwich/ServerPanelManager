package dev.neuralnexus.serverpanelmanager.bungee.listeners.player;

import dev.neuralnexus.serverpanelmanager.bungee.player.BungeePlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerServerSwitchListener;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Listens for player server switches.
 */
public class BungeePlayerServerSwitchListener implements Listener, SPMPlayerServerSwitchListener {
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
        SPMServerSwitch(new BungeePlayer(player), toServer);
    }
}
