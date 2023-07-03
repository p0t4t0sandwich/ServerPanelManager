package dev.neuralnexus.serverpanelmanager.fabric.mixin.listeners.player;

import dev.neuralnexus.serverpanelmanager.common.listneners.player.SPMPlayerLoginListener;
import dev.neuralnexus.serverpanelmanager.fabric.events.player.FabricPlayerLoginEvent;
import dev.neuralnexus.serverpanelmanager.fabric.player.FabricPlayer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Listens for player logins and adds the player to the cache.
 * Also fires the FabricPlayerLoginEvent.
 */
@Mixin(ServerLoginNetworkHandler.class)
public class FabricPlayerLoginListener implements SPMPlayerLoginListener {
    /**
     * Called when a player logs in.
     * @param ci The callback info.
     */
    @Inject(method = "addToServer", at = @At("HEAD"))
    private void onPlayerLogin(ServerPlayerEntity player, CallbackInfo ci) {
        // Pass player to helper function
        SPMPlayerLogin(new FabricPlayer(player));

        // Fire the login event
        FabricPlayerLoginEvent.EVENT.invoker().onPlayerLogin(player);
    }
}
