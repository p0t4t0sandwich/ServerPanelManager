package dev.neuralnexus.serverpanelmanager.bungee;

import dev.neuralnexus.serverpanelmanager.bungee.commands.BungeeSPMCommand;
import dev.neuralnexus.serverpanelmanager.bungee.listeners.BungeePlayerLoginListener;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

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
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerListener(this, new BungeePlayerLoginListener());
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
        pluginStop();
    }
}
