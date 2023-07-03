package dev.neuralnexus.serverpanelmanager.common.listneners.player;

import dev.neuralnexus.serverpanelmanager.common.player.AbstractPlayer;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Listens for player advancements.
 */
public interface SPMPlayerAdvancementListener {
    /**
     * Called when a player makes an advancement.
     * @param player The player.
     * @param advancement The advancement.
     */
    default void SPMPlayerAdvancement(AbstractPlayer player, String advancement) {
        runTaskAsync(() -> {
            try {
                // TODO: Add trigger/task system
                // relay.sendSystemMessage(player.getServerName(), player.getDisplayName() + " has made the advancement [" + advancement + "]");
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
