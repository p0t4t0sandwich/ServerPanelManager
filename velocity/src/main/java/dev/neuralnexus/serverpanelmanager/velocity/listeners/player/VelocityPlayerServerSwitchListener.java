package dev.neuralnexus.serverpanelmanager.velocity.listeners.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerServerSwitchListener;
import dev.neuralnexus.serverpanelmanager.velocity.player.VelocityPlayer;

/**
 * Listens for player server switch.
 */
public class VelocityPlayerServerSwitchListener implements SPMPlayerServerSwitchListener {
    /**
     * Called when a player switches servers.
     * @param event The player server switch event
     */
    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        // If player is just joining, don't run this function
        if (!event.getPreviousServer().isPresent()) return;

        // Get Player and current server
        Player player = event.getPlayer();
        String toServer = event.getServer().getServerInfo().getName();

        // Pass Player and current server to helper function
        SPMServerSwitch(new VelocityPlayer(player), toServer);
    }
}
