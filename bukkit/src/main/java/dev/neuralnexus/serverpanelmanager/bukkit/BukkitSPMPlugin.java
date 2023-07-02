package dev.neuralnexus.serverpanelmanager.bukkit;

import dev.neuralnexus.serverpanelmanager.bukkit.commands.BukkitSPMCommand;
import dev.neuralnexus.serverpanelmanager.bukkit.listeners.BukkitPlayerLoginListener;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static dev.neuralnexus.serverpanelmanager.common.Utils.getBukkitServerType;

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
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BukkitPlayerLoginListener(), this);
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
        pluginStop();
    }
}
