package dev.neuralnexus.serverpanelmanager.forge.listeners.player;

import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerMessageListener;
import dev.neuralnexus.serverpanelmanager.forge.player.ForgePlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for player messages.
 */
public class ForgePlayerMessageListener implements SPMPlayerMessageListener {
    /**
     * Called when a player sends a message.
     * @param event The player message event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    void onPlayerMessage(ServerChatEvent event) {
        // Pass message to helper function
        SPMPlayerMessage(new ForgePlayer(event.getPlayer()), event.getMessage().getString(), event.isCanceled());
    }
}
