package dev.neuralnexus.serverpanelmanager.common.listneners.player;

import dev.neuralnexus.serverpanelmanager.common.player.AbstractPlayer;
import dev.neuralnexus.serverpanelmanager.common.player.cache.PlayerCache;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Listens for proxy server switches and updates the TaterPlayer's server name.
 */
public interface SPMPlayerServerSwitchListener {
    /**
     * Called when a player logs out, and sends it to the message relay.
     * @param player The player.
     */
    static void onServerSwitch(AbstractPlayer player, String toServer) {
        runTaskAsync(() -> {
            try {
                // Get TaterPlayer from cache
                AbstractPlayer cachedTaterPlayer = PlayerCache.getPlayerFromCache(player.getUUID());
                if (cachedTaterPlayer == null) {
                    return;
                }

                // Get fromServer
                String fromServer = player.getServerName();

                // Update the server name and TaterPlayer object
                player.setServerName(toServer);
                PlayerCache.setPlayerInCache(player.getUUID(), player);

                // TODO: Add trigger/task system
                // Relay the server switch message (player, fromServer, toServer);
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
