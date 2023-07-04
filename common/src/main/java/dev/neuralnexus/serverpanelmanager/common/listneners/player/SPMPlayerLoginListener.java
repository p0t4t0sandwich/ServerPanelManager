package dev.neuralnexus.serverpanelmanager.common.listneners.player;

import dev.neuralnexus.serverpanelmanager.common.player.AbstractPlayer;
import dev.neuralnexus.serverpanelmanager.common.player.cache.PlayerCache;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Listens for player logins and adds the player to the cache.
 */
public interface SPMPlayerLoginListener {
    /**
     * Called when a player logs in.
     * @param player The player.
     */
    static void onPlayerLogin(AbstractPlayer player) {
        runTaskAsync(() -> {
            try {
                // Add the player to the cache
                PlayerCache.setPlayerInCache(player.getUUID(), player);

                // TODO: Add trigger/task system
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
