package dev.neuralnexus.serverpanelmanager.common.listneners.player;

import dev.neuralnexus.serverpanelmanager.common.player.AbstractPlayer;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Listens for player deaths.
 */
public interface SPMPlayerDeathListener {
    /**
     * Called when a player dies, and sends it to the message relay.
     * @param player The player.
     * @param deathMessage The death message.
     */
    static void onPlayerDeath(AbstractPlayer player, String deathMessage) {
        runTaskAsync(() -> {
            try {
                // TODO: Add trigger/task system
                // relay.sendSystemMessage(player.getServerName(), deathMessage);
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
