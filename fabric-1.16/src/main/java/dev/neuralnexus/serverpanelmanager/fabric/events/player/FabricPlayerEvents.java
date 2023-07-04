package dev.neuralnexus.serverpanelmanager.fabric.events.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Contains additional player events.
 */
public final class FabricPlayerEvents {
    /**
     * Called when a player finishes an advancement.
     */
    public static final Event<PlayerAdvancement> ADVANCEMENT = EventFactory.createArrayBacked(PlayerAdvancement.class, (listeners) -> (player, advancement) -> {
        for (PlayerAdvancement listener : listeners) {
            listener.onPlayerAdvancement(player, advancement);
        }
    });

    /**
     * Called when a player dies.
     */
    public static final Event<PlayerDeath> DEATH = EventFactory.createArrayBacked(PlayerDeath.class, (listeners) -> (player, source) -> {
        for (PlayerDeath listener : listeners) {
            listener.onPlayerDeath(player, source);
        }
    });

    /**
     * Called when a player sends a message.
     */
    public static final Event<PlayerMessage> MESSAGE = EventFactory.createArrayBacked(PlayerMessage.class, (listeners) -> (player, message, isCanceled) -> {
        for (PlayerMessage listener : listeners) {
            listener.onPlayerMessage(player, message, isCanceled);
        }
    });

    @FunctionalInterface
    public interface PlayerAdvancement {
        void onPlayerAdvancement(PlayerEntity player, Advancement advancement);
    }

    @FunctionalInterface
    public interface PlayerDeath {
        void onPlayerDeath(PlayerEntity player, DamageSource source);
    }

    @FunctionalInterface
    public interface PlayerMessage {
        void onPlayerMessage(PlayerEntity player, String message, boolean isCanceled);
    }
}
