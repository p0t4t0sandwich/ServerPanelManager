package dev.neuralnexus.serverpanelmanager.forge.listeners.player;

import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerDeathListener;
import dev.neuralnexus.serverpanelmanager.forge.player.ForgePlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for player deaths.
 */
public class ForgePlayerDeathListener implements SPMPlayerDeathListener {
    /**
     * Called when a player dies.
     * @param event The player death event
     */
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        // Send death message through relay
        SPMPlayerDeath(new ForgePlayer((Player) entity), event.getSource().getLocalizedDeathMessage(entity).getString());
    }
}
