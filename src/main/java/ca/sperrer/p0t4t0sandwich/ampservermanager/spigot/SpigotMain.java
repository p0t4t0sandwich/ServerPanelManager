package ca.sperrer.p0t4t0sandwich.ampservermanager.spigot;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SpigotMain extends JavaPlugin {
    public AMPServerManager ampServerManager;

    // Singleton instance
    private static SpigotMain instance;
    public static SpigotMain getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Singleton instance
        instance = this;

        // Start AMPAPAI Server Manager
        ampServerManager = new AMPServerManager("plugins", getLogger());

        new BukkitRunnable() {
            @Override
            public void run() {
                ampServerManager.start();
            }
        }.runTask(this);

        // Register commands
        getCommand("amp").setExecutor(new SpigotAMPCommands());

        // Plugin enable message
        getLogger().info("AMPAPAI Server Manager has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin disable message
        getLogger().info("AMPAPAI Server Manager has been disabled!");
    }
}
