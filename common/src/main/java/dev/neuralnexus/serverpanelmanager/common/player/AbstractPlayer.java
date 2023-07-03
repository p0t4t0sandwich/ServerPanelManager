package dev.neuralnexus.serverpanelmanager.common.player;

import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;

import java.util.UUID;

/**
 * The interface for a AbstractPlayer
 */
public interface AbstractPlayer {
    /**
     * Get the UUID of the player
     * @return The UUID of the player
     */
    UUID getUUID();

    /**
     * Get the name of the player
     * @return The name of the player
     */
    String getName();

    /**
     * Get the display name of the player
     * @return The display name of the player
     */
    String getDisplayName();

    /**
     * Get the server the player is on
     * @return The server the player is on
     */
    String getServerName();

    /**
     * Set the server the player is on
     * @param serverName The server the player is on
     */
    void setServerName(String serverName);

    /**
     * Send a message to the player
     * @param message The message to send
     */
    void sendMessage(String message);

    /**
     * Get the prefix of the player
     * @return The prefix of the player
     */
    default String getPrefix() {
        if (!LuckPermsHook.isHooked()) return "";
        LuckPermsHook luckPermsHook = LuckPermsHook.getInstance();
        String suffix = luckPermsHook.getPrefix(getUUID());
        return suffix != null ? suffix : "";
    }

    /**
     * Get the suffix of the player
     * @return The suffix of the player
     */
    default String getSuffix() {
        if (!LuckPermsHook.isHooked()) return "";
        LuckPermsHook luckPermsHook = LuckPermsHook.getInstance();
        String suffix = luckPermsHook.getSuffix(getUUID());
        return suffix != null ? suffix : "";
    }

    /**
     * Check if the player has a permission
     * @param permission The permission to check
     * @return Whether the player has the permission
     */
    default boolean hasPermission(String permission) {
        if (!LuckPermsHook.isHooked()) return false;
        LuckPermsHook luckPermsHook = LuckPermsHook.getInstance();
        return luckPermsHook.playerHasPermission(getUUID(), permission);
    }
}