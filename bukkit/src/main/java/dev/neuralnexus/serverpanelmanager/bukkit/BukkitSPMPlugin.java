package dev.neuralnexus.serverpanelmanager.bukkit;

import dev.neuralnexus.serverpanelmanager.bukkit.commands.BukkitSPMCommand;
import dev.neuralnexus.serverpanelmanager.bukkit.listeners.paper.server.PaperServerListener;
import dev.neuralnexus.serverpanelmanager.bukkit.listeners.player.BukkitPlayerListener;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static dev.neuralnexus.serverpanelmanager.common.Utils.getBukkitServerType;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskLaterAsync;

/**
 * The ServerPanelManager Bukkit plugin.
 */
public class BukkitSPMPlugin extends JavaPlugin implements ServerPanelManagerPlugin {
    /**
     * Use whatever logger is being used.
     * @param message The message to log
     */
    public void useLogger(String message) {
        getLogger().info(message);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String pluginConfigPath() {
        return "plugins";
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getServerType() {
        return getBukkitServerType();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerHooks() {
        // Register LuckPerms hook
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            useLogger("[ServerPanelManager] LuckPerms detected, enabling LuckPerms hook.");
            ServerPanelManager.addHook(new LuckPermsHook());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerEventListeners() {
        // Register player event listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BukkitPlayerListener(), this);

        // Register server event listeners
        SPMServerStartingListener.onServerStarting();

        if (getServerType().equals("Paper")) {
            pluginManager.registerEvents(new PaperServerListener(), this);
        } else {
            runTaskLaterAsync(SPMServerStartedListener::onServerStarted, 100L);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerCommands() {
        getCommand("spm").setExecutor(new BukkitSPMCommand());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onEnable() {
        pluginStart();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDisable() {
        // Server stopped listener
        SPMServerStoppedListener.onServerStopped();

        pluginStop();
    }
}
