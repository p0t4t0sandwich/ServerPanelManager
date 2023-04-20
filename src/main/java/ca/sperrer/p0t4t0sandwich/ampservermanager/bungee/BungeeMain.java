package ca.sperrer.p0t4t0sandwich.ampservermanager.bungee;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class BungeeMain extends Plugin {
    public AMPServerManager ampServerManager;

    // Get server type
    public String getServerType() {
        return "BungeeCord";
    }

    // Singleton instance
    private static BungeeMain instance;
    public static BungeeMain getInstance() {
        return instance;
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
        getProxy().getPluginManager().registerCommand(this, new BungeeAMPCommands());

        // Plugin enable message
        getLogger().info("AMPAPAI Server Manager has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin disable message
        getLogger().info("AMPAPAI Server Manager has been disabled!");
    }
}
