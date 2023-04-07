package ca.sperrer.p0t4t0sandwich.ampservermanager.bukkit;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitMain extends JavaPlugin {
    public AMPServerManager ampServerManager;

    // Singleton instance
    private static BukkitMain instance;
    public static BukkitMain getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Singleton instance
        instance = this;

        // Start AMPAPAI Server Manager
        ampServerManager = new AMPServerManager("plugins", getLogger());

        // TODO: Set up methods to utilize different API's schedulers based on the server type


//        new BukkitRunnable() {
//            @Override
//            public void run() {
        (new Thread(() -> ampServerManager.start())).start();
//            }
//        }.runTask(this);

        // Register commands
        getCommand("amp").setExecutor(new BukkitAMPCommands());

        // Plugin enable message
        getLogger().info("AMPAPAI Server Manager has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin disable message
        getLogger().info("AMPAPAI Server Manager has been disabled!");
    }
}
