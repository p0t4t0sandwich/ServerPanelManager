package ca.sperrer.p0t4t0sandwich.ampservermanager.bungee;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class BungeeMain extends Plugin {
    public static YamlDocument config;
    public static BungeeAMPServerManager ampServerManager;

    // Singleton instance
    private static BungeeMain instance;
    public static BungeeMain getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        // Config
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResourceAsStream("config.yml"));
            config.reload();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Singleton instance
        instance = this;

        // Start AMPAPAI Server Manager
        ampServerManager = new BungeeAMPServerManager(this, config);

        ScheduledTask scheduledTask = getProxy().getScheduler().schedule(this, () ->
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
