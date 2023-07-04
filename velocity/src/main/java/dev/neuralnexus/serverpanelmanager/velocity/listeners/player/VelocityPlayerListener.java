package dev.neuralnexus.serverpanelmanager.velocity.listeners.player;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLoginListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLogoutListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerMessageListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerServerSwitchListener;
import dev.neuralnexus.serverpanelmanager.velocity.player.VelocityPlayer;

/**
 * Listens for player events.
 */
public class VelocityPlayerListener {
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
        SPMPlayerLoginListener.onPlayerLogin(abstractPlayer);
    }

    /**
     * Called when a player logs out.
     * @param event The player logout event
     */
    @Subscribe
    public void onPlayerLogout(DisconnectEvent event) {
        // Pass player to helper function
        SPMPlayerLogoutListener.onPlayerLogout(new VelocityPlayer(event.getPlayer()));
    }

    /**
     * Called when a player sends a message.
     * @param event The player message event
     */
    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerMessage(PlayerChatEvent event) {
        // Get player and message
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (!player.getCurrentServer().isPresent()) return;

        // Pass message to helper function
        SPMPlayerMessageListener.onPlayerMessage(new VelocityPlayer(player), message, event.getResult() == PlayerChatEvent.ChatResult.denied());
    }

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
        SPMPlayerServerSwitchListener.onServerSwitch(new VelocityPlayer(player), toServer);
    }
}
