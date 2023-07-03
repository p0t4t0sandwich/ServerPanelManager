package dev.neuralnexus.serverpanelmanager.velocity.listeners.player;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerMessageListener;
import dev.neuralnexus.serverpanelmanager.velocity.player.VelocityPlayer;

/**
 * Listens for player messages.
 */
public class VelocityPlayerMessageListener implements SPMPlayerMessageListener {
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
        SPMPlayerMessage(new VelocityPlayer(player), message, event.getResult() == PlayerChatEvent.ChatResult.denied());
    }
}
