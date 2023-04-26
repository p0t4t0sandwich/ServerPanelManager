package ca.sperrer.p0t4t0sandwich.panelservermanager.bukkit;

import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.PanelServerManager;
import static ca.sperrer.p0t4t0sandwich.panelservermanager.Utils.isFolia;
import static ca.sperrer.p0t4t0sandwich.panelservermanager.Utils.isPaper;
import static ca.sperrer.p0t4t0sandwich.panelservermanager.Utils.isSpigot;
import static ca.sperrer.p0t4t0sandwich.panelservermanager.Utils.isCraftBukkit;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class BukkitMain extends JavaPlugin {
    public PanelServerManager panelServerManager;

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

        getLogger().info("Panel Server Manager is running on " + getServerType() + ".");

        // Start Panel Server Manager
        panelServerManager = new PanelServerManager("plugins", getLogger());
        panelServerManager.start();

        // Register commands
        Objects.requireNonNull(getCommand("amp")).setExecutor(new BukkitAMPCommands());

        // Plugin enable message
        getLogger().info("Panel Server Manager has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin disable message
        getLogger().info("Panel Server Manager has been disabled!");
    }
}
