package dev.neuralnexus.serverpanelmanager.fabric.mixin.listeners.player;

import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLogoutListener;
import dev.neuralnexus.serverpanelmanager.fabric.events.player.FabricPlayerLogoutEvent;
import dev.neuralnexus.serverpanelmanager.fabric.player.FabricPlayer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Listens for player logouts and removes the player from the cache.
 * Also fires the FabricPlayerLogoutEvent.
 */
@Mixin(ServerLoginNetworkHandler.class)
public class FabricPlayerLogoutListener implements SPMPlayerLogoutListener {
    /**
     * Called when a player logs out.
     * @param ci The callback info.
     */
    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onPlayerLogout(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Pass player to helper function
        SPMPlayerLogout(new FabricPlayer(player));

        // Fire the logout event
        FabricPlayerLogoutEvent.EVENT.invoker().onPlayerLogout(player);
    }
}
