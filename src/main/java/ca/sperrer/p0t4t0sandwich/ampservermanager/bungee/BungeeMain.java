package ca.sperrer.p0t4t0sandwich.ampservermanager.bungee;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class BungeeMain extends Plugin {
    public AMPServerManager ampServerManager;

    // Singleton instance
    private static BungeeMain instance;
    public static BungeeMain getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        // Singleton instance
        instance = this;

        // Start AMPAPAI Server Manager
        ampServerManager = new AMPServerManager("plugins", getLogger());

        getProxy().getScheduler().schedule(this, () ->
                ampServerManager.start(), 0, TimeUnit.SECONDS
        );

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
