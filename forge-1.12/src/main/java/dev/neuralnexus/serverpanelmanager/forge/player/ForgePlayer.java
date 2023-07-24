package dev.neuralnexus.serverpanelmanager.forge.player;

import dev.neuralnexus.serverpanelmanager.common.player.AbstractPlayer;
import jdk.javadoc.internal.doclets.formats.html.markup.Text;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

/**
 * Abstracts a Forge player to an AbstractPlayer.
 */
public class ForgePlayer implements AbstractPlayer {
    private final EntityPlayer player;
    private String serverName;

    /**
     * Constructor.
     * @param player The Forge player.
     */
    public ForgePlayer(EntityPlayer player) {
        this.player = player;
        this.serverName = "local";
    }

    /**
     * @inheritDoc
     */
    @Override
    public java.util.UUID getUUID() {
        return player.getUniqueID();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getName() {
        return player.getName();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getDisplayName() {
        return player.getDisplayName().toString();
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
        player.sendMessage((ITextComponent) Text.of(message));
    }
}
