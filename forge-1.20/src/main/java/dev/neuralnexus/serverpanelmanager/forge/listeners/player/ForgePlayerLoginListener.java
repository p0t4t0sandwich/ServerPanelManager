package dev.neuralnexus.serverpanelmanager.forge.listeners.player;

import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLoginListener;
import dev.neuralnexus.serverpanelmanager.forge.player.ForgePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for player logins.
 */
public class ForgePlayerLoginListener implements SPMPlayerLoginListener {
    /**
     * Called when a player logs in.
     * @param event The player login event
     */
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Pass player to helper function
        SPMPlayerLogin(new ForgePlayer(event.getEntity()));
    }
}
