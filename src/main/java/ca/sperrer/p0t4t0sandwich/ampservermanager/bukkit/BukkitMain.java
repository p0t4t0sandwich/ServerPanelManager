package ca.sperrer.p0t4t0sandwich.ampservermanager.bukkit;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import static ca.sperrer.p0t4t0sandwich.ampservermanager.VersionUtils.isFolia;
import static ca.sperrer.p0t4t0sandwich.ampservermanager.VersionUtils.isPaper;
import static ca.sperrer.p0t4t0sandwich.ampservermanager.VersionUtils.isSpigot;
import static ca.sperrer.p0t4t0sandwich.ampservermanager.VersionUtils.isCraftBukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class BukkitMain extends JavaPlugin {
    public AMPServerManager ampServerManager;

    // Singleton instance
    private static BukkitMain instance;
    public static BukkitMain getInstance() {
        return instance;
    }

    public String getServerType() {
        if (isFolia()) {
            return "Folia";
        } else if (isPaper()) {
            return "Paper";
        } else if (isSpigot()) {
            return "Spigot";
        } else if (isCraftBukkit()) {
            return "CraftBukkit";
        } else {
            return "Unknown";
        }
    }

    @Override
    public void onEnable() {
        // Singleton instance
        instance = this;

        getLogger().info("AMPAPAI Server Manager is running on " + getServerType() + ".");

        // Start AMPAPAI Server Manager
        ampServerManager = new AMPServerManager("plugins", getLogger());
        ampServerManager.start();

        // Register commands
        Objects.requireNonNull(getCommand("amp")).setExecutor(new BukkitAMPCommands());

        // Plugin enable message
        getLogger().info("AMPAPAI Server Manager has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin disable message
        getLogger().info("AMPAPAI Server Manager has been disabled!");
    }
}
