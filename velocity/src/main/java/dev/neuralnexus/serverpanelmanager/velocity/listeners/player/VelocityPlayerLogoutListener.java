package dev.neuralnexus.serverpanelmanager.velocity.listeners.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLogoutListener;
import dev.neuralnexus.serverpanelmanager.velocity.player.VelocityPlayer;

/**
 * Listens for player logouts.
 */
public class VelocityPlayerLogoutListener implements SPMPlayerLogoutListener {
    /**
     * Called when a player logs out.
     * @param event The player logout event
     */
    @Subscribe
    public void onPlayerLogout(DisconnectEvent event) {
        // Pass player to helper function
        SPMPlayerLogout(new VelocityPlayer(event.getPlayer()));
    }
}
