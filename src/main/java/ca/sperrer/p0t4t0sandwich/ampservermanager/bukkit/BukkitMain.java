package ca.sperrer.p0t4t0sandwich.ampservermanager.bukkit;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import static ca.sperrer.p0t4t0sandwich.ampservermanager.VersionUtils.*;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

public class BukkitMain extends JavaPlugin {
    public AMPServerManager ampServerManager;

    // Singleton instance
    private static BukkitMain instance;
    public static BukkitMain getInstance() {
        return instance;
    }

    public static boolean FOLIA = isFolia();

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

    public static void runTaskAsync(Plugin plugin, Runnable run) {
        if (!FOLIA) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, run);
            return;
        }
        ForkJoinPool.commonPool().submit(run);
    }

    @Override
    public void onEnable() {
        // Singleton instance
        instance = this;

        // Folia check
        if (FOLIA) {
            getLogger().info("Folia detected, using our own scheduler");
        } else {
            getLogger().info(getServerType() + " detected, using the Bukkit scheduler");
        }

        // Start AMPAPAI Server Manager
        ampServerManager = new AMPServerManager("plugins", getLogger());
        runTaskAsync(this, ampServerManager::start);

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
