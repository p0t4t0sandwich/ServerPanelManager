package dev.neuralnexus.serverpanelmanager.bukkit;

import dev.neuralnexus.serverpanelmanager.bukkit.commands.BukkitSPMCommand;
import dev.neuralnexus.serverpanelmanager.bukkit.listeners.player.*;
import dev.neuralnexus.serverpanelmanager.bukkit.listeners.server.BukkitServerStartedListener;
import dev.neuralnexus.serverpanelmanager.bukkit.listeners.server.BukkitServerStartingListener;
import dev.neuralnexus.serverpanelmanager.bukkit.listeners.server.BukkitServerStoppedListener;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static dev.neuralnexus.serverpanelmanager.common.Utils.getBukkitServerType;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskLaterAsync;

/**
 * The ServerPanelManager Bukkit plugin.
 */
public class BukkitSPMPlugin extends JavaPlugin implements ServerPanelManagerPlugin {
    /**
     * @inheritDoc
     */
    @Override
    public Object pluginLogger() {
        return getLogger();
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
        pluginManager.registerEvents(new BukkitPlayerAdvancementListener(), this);
        pluginManager.registerEvents(new BukkitPlayerDeathListener(), this);
        pluginManager.registerEvents(new BukkitPlayerLoginListener(), this);
        pluginManager.registerEvents(new BukkitPlayerLogoutListener(), this);
        pluginManager.registerEvents(new BukkitPlayerMessageListener(), this);

        // Register server event listeners
        new BukkitServerStartingListener().onServerStarting();
        runTaskLaterAsync(() -> new BukkitServerStartedListener().onServerStarted(), 100L);
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
        new BukkitServerStoppedListener().onServerStopped();

        pluginStop();
    }
}
