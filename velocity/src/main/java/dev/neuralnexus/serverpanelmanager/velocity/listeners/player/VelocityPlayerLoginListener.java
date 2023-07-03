package dev.neuralnexus.serverpanelmanager.velocity.listeners.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLoginListener;
import dev.neuralnexus.serverpanelmanager.velocity.player.VelocityPlayer;

/**
 * Listens for player logins.
 */
public class VelocityPlayerLoginListener implements SPMPlayerLoginListener {
    /**
     * Called when a player logs in.
     * @param event The player login event
     */
    @Subscribe
    public void onPlayerLogin(ServerConnectedEvent event) {
        // If player is switching servers, don't run this function
        if (event.getPreviousServer().isPresent()) return;

        // Get Player and current server
        Player player = event.getPlayer();
        String toServer = event.getServer().getServerInfo().getName();

        VelocityPlayer abstractPlayer = new VelocityPlayer(player);
        abstractPlayer.setServerName(toServer);

        // Pass player to helper function
        SPMPlayerLogin(abstractPlayer);
    }
}
