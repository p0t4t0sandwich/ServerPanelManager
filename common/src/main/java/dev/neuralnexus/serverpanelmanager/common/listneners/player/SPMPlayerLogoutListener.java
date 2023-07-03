package dev.neuralnexus.serverpanelmanager.common.listneners.player;

import dev.neuralnexus.serverpanelmanager.common.player.AbstractPlayer;
import dev.neuralnexus.serverpanelmanager.common.player.cache.PlayerCache;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Listens for player logouts and removes the player from the cache.
 */
public interface SPMPlayerLogoutListener {
    /**
     * Called when a player logs out.
     * @param player The player.
     */
    default void SPMPlayerLogout(AbstractPlayer player) {
        runTaskAsync(() -> {
            try {
                // Remove the player from the cache
                PlayerCache.removePlayerFromCache(player.getUUID());

                // TODO: Add trigger/task system
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
