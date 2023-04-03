package ca.sperrer.p0t4t0sandwich.ampservermanager.spigot;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class SpigotMain extends JavaPlugin {
    public static YamlDocument config;
    public static SpigotAMPServerManager ampServerManager;

    // Singleton instance
    private static SpigotMain instance;
    public static SpigotMain getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Config
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"));
            config.reload();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Singleton instance
        instance = this;

        // Start AMPAPAI Server Manager
        ampServerManager = new SpigotAMPServerManager(this, config);

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
