package dev.neuralnexus.serverpanelmanager.forge.listeners.player;

import dev.neuralnexus.serverpanelmanager.common.listneners.player.*;
import dev.neuralnexus.serverpanelmanager.forge.player.ForgePlayer;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Listens for player events.
 */
public class ForgePlayerListener {
    /**
     * Called when a player advances.
     * @param event The advancement event
     */
    @SubscribeEvent
    public void onPlayerAdvancement(AdvancementEvent event) {
        DisplayInfo display = event.getAdvancement().getDisplay();
        if (display != null && display.shouldAnnounceChat()) {
            // Pass advancement to helper function
            SPMPlayerAdvancementListener.onPlayerAdvancement(new ForgePlayer(event.getPlayer()), display.getTitle().getString());
        }
    }

    /**
     * Called when a player dies.
     * @param event The player death event
     */
    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof Player)) return;
        // Pass death message to helper function
        SPMPlayerDeathListener.onPlayerDeath(new ForgePlayer((Player) entity), event.getSource().getLocalizedDeathMessage(entity).getString());
    }

    /**
     * Called when a player logs in.
     * @param event The player login event
     */
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Pass player to helper function
        SPMPlayerLoginListener.onPlayerLogin(new ForgePlayer(event.getPlayer()));
    }

    /**
     * Called when a player logs out.
     * @param event The player logout event
     */
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // Pass player to helper function
        SPMPlayerLogoutListener.onPlayerLogout(new ForgePlayer(event.getPlayer()));
    }

    /**
     * Called when a player sends a message.
     * @param event The player message event
     */
    @SubscribeEvent
    void onPlayerMessage(ServerChatEvent event) {
        // Pass message to helper function
        SPMPlayerMessageListener.onPlayerMessage(new ForgePlayer(event.getPlayer()), event.getMessage(), event.isCanceled());
    }
}
