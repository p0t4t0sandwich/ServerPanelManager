package dev.neuralnexus.serverpanelmanager.forge.listeners.player;

import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLogoutListener;
import dev.neuralnexus.serverpanelmanager.forge.player.ForgePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for player logouts.
 */
public class ForgePlayerLogoutListener implements SPMPlayerLogoutListener {
    /**
     * Called when a player logs out.
     * @param event The player logout event
     */
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // Pass player to helper function
        SPMPlayerLogout(new ForgePlayer(event.getEntity()));
    }
}
