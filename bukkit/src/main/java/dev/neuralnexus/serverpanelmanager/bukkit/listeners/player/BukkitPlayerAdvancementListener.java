package dev.neuralnexus.serverpanelmanager.bukkit.listeners.player;

import dev.neuralnexus.serverpanelmanager.bukkit.player.BukkitPlayer;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerAdvancementListener;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

/**
 * Listens for player advancements.
 */
public class BukkitPlayerAdvancementListener implements Listener, SPMPlayerAdvancementListener {
    /**
     * Called when a player completes an advancement.
     * @param event The event.
     */
    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceChat()) {
            // Pass advancement to helper function
            SPMPlayerAdvancement(new BukkitPlayer(event.getPlayer()), advancement.getDisplay().getTitle());
        }
    }
}
