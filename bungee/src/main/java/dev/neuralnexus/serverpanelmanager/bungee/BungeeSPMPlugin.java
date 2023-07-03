package dev.neuralnexus.serverpanelmanager.bungee;

import dev.neuralnexus.serverpanelmanager.bungee.commands.BungeeSPMCommand;
import dev.neuralnexus.serverpanelmanager.bungee.listeners.player.BungeePlayerLoginListener;
import dev.neuralnexus.serverpanelmanager.bungee.listeners.player.BungeePlayerLogoutListener;
import dev.neuralnexus.serverpanelmanager.bungee.listeners.player.BungeePlayerMessageListener;
import dev.neuralnexus.serverpanelmanager.bungee.listeners.player.BungeePlayerServerSwitchListener;
import dev.neuralnexus.serverpanelmanager.bungee.listeners.server.BungeeServerStartedListener;
import dev.neuralnexus.serverpanelmanager.bungee.listeners.server.BungeeServerStartingListener;
import dev.neuralnexus.serverpanelmanager.bungee.listeners.server.BungeeServerStoppedListener;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskLaterAsync;

/**
 * The ServerPanelManager BungeeCord plugin.
 */
public class BungeeSPMPlugin extends Plugin implements ServerPanelManagerPlugin {
    private static ProxyServer proxyServer;
    /**
     * Get the proxy server.
     * @return The proxy server.
     */
    public static ProxyServer getProxyServer() {
        return proxyServer;
    }

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
        return "BungeeCord";
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerHooks() {
        // Register LuckPerms hook
        if (getProxy().getPluginManager().getPlugin("LuckPerms") != null) {
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
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerListener(this, new BungeePlayerLoginListener());
        pluginManager.registerListener(this, new BungeePlayerLogoutListener());
        pluginManager.registerListener(this, new BungeePlayerMessageListener());
        pluginManager.registerListener(this, new BungeePlayerServerSwitchListener());

        // Register server event listeners
        new BungeeServerStartingListener().onServerStarting();
        runTaskLaterAsync(() -> new BungeeServerStartedListener().onServerStarted(), 100L);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerCommands() {
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new BungeeSPMCommand());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onEnable() {
        proxyServer = getProxy();
        pluginStart();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDisable() {
        // Server stopped listener
        new BungeeServerStoppedListener().onServerStopped();

        pluginStop();
    }
}
