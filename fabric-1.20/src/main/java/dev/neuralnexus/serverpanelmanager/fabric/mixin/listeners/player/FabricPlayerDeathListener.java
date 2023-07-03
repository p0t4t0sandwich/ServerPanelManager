package dev.neuralnexus.serverpanelmanager.fabric.mixin.listeners.player;

import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerDeathListener;
import dev.neuralnexus.serverpanelmanager.fabric.events.player.FabricPlayerDeathEvent;
import dev.neuralnexus.serverpanelmanager.fabric.player.FabricPlayer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Listens for player deaths and emits an event.
 */
@Mixin(PlayerEntity.class)
public class FabricPlayerDeathListener implements SPMPlayerDeathListener {
    /**
     * Called when a player dies.
     * @param source The source of the damage.
     * @param ci The callback info.
     */
    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onPlayerDeath(DamageSource source, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // Fire the death event
        FabricPlayerDeathEvent.EVENT.invoker().onPlayerDeath(player, source);

        // Pass the death to the helper function
        SPMPlayerDeath(new FabricPlayer(player), source.getDeathMessage(player).getString());
    }
}
