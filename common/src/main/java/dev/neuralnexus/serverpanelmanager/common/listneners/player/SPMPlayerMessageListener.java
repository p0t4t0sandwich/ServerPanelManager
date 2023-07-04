package dev.neuralnexus.serverpanelmanager.common.listneners.player;

import dev.neuralnexus.serverpanelmanager.common.player.AbstractPlayer;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Listens for player messages.
 */
public interface SPMPlayerMessageListener {
    /**
     * Called when a player sends a message, and sends it to the message relay.
     * @param player The player
     * @param message The message
     * @param isCancelled Whether the message was cancelled
     */
    static void onPlayerMessage(AbstractPlayer player, String message, boolean isCancelled) {
        runTaskAsync(() -> {
            try {
                // TODO: Add trigger/task system
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
