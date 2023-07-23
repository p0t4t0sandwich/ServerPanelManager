package dev.neuralnexus.serverpanelmanager.forge.player;

import dev.neuralnexus.serverpanelmanager.common.player.AbstractPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

/**
 * Abstracts a Forge player to an AbstractPlayer.
 */
public class ForgePlayer implements AbstractPlayer {
    private final Player player;
    private String serverName;

    /**
     * Constructor.
     * @param player The Forge player.
     */
    public ForgePlayer(Player player) {
        this.player = player;
        this.serverName = "local";
    }

    /**
     * @inheritDoc
     */
    @Override
    public java.util.UUID getUUID() {
        return player.getUUID();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getName() {
        return player.getName().getString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getDisplayName() {
        return player.getDisplayName().getString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getServerName() {
        return serverName;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setServerName(String server) {
        this.serverName = server;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void sendMessage(String message) {
        player.sendMessage(new TranslatableComponent(message), getUUID());
    }
}
