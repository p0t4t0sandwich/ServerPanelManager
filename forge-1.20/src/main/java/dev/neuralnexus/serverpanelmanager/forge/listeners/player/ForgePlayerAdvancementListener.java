package dev.neuralnexus.serverpanelmanager.forge.listeners.player;

import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerAdvancementListener;
import dev.neuralnexus.serverpanelmanager.forge.player.ForgePlayer;
import net.minecraft.advancements.DisplayInfo;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for player advancements.
 */
public class ForgePlayerAdvancementListener implements SPMPlayerAdvancementListener {
    /**
     * Called when a player advances.
     * @param event The advancement event
     */
    @SubscribeEvent
    public void onPlayerAdvancement(AdvancementEvent event) {
        DisplayInfo display = event.getAdvancement().getDisplay();
        if (display != null && display.shouldAnnounceChat()) {
            // Pass advancement to helper function
            SPMPlayerAdvancement(new ForgePlayer(event.getEntity()), display.getTitle().getString());
        }
    }
}
