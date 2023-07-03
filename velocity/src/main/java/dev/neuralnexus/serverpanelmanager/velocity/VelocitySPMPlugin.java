package dev.neuralnexus.serverpanelmanager.velocity;

import com.velocitypowered.api.plugin.Dependency;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import dev.neuralnexus.serverpanelmanager.velocity.commands.VelocitySPMCommand;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.velocity.listeners.player.VelocityPlayerLoginListener;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.neuralnexus.serverpanelmanager.velocity.listeners.player.VelocityPlayerLogoutListener;
import dev.neuralnexus.serverpanelmanager.velocity.listeners.player.VelocityPlayerMessageListener;
import dev.neuralnexus.serverpanelmanager.velocity.listeners.player.VelocityPlayerServerSwitchListener;
import dev.neuralnexus.serverpanelmanager.velocity.listeners.server.VelocityServerStartedListener;
import dev.neuralnexus.serverpanelmanager.velocity.listeners.server.VelocityServerStartingListener;
import dev.neuralnexus.serverpanelmanager.velocity.listeners.server.VelocityServerStoppedListener;
import org.slf4j.Logger;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskLaterAsync;

/**
 * The ServerPanelManager Velocity plugin.
 */
@Plugin(
        id = "serverpanelmanager",
        name = "ServerPanelManager",
        version = "1.0.0",
        authors = "p0t4t0sandwich",
        description = "A plugin that allows you to manage your Panel's game servers from within minecraft",
        url = "https://github.com/p0t4t0sandwich/ServerPanelManager",
        dependencies = {
                @Dependency(id = "luckperms", optional = true)
        }
)
public class VelocitySPMPlugin implements ServerPanelManagerPlugin {
    @Inject private ProxyServer server;
    @Inject private Logger logger;

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
        return logger;
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
        return "Velocity";
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerHooks() {
        // Register LuckPerms hook
        if (server.getPluginManager().getPlugin("LuckPerms").isPresent()) {
            useLogger("[ServerPanelManager] LuckPerms detected, enabling LuckPerms hook.");
            ServerPanelManager.addHook(new LuckPermsHook());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerEventListeners() {
        EventManager eventManager = server.getEventManager();

        // Register player event listeners
        eventManager.register(this, new VelocityPlayerLoginListener());
        eventManager.register(this, new VelocityPlayerLogoutListener());
        eventManager.register(this, new VelocityPlayerMessageListener());
        eventManager.register(this, new VelocityPlayerServerSwitchListener());

        // Register server event listeners
        eventManager.register(this, new VelocityServerStoppedListener());
        new VelocityServerStartingListener().onServerStarting();
        runTaskLaterAsync(() -> new VelocityServerStartedListener().onServerStarted(), 100L);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerCommands() {
        CommandManager commandManager = server.getCommandManager();
        commandManager.register("spmv", new VelocitySPMCommand());
    }

    /**
     * Called when the proxy is initialized.
     * @param event The event.
     */
    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxyServer = server;
        pluginStart();
    }

    /**
     * Called when the proxy is shutting down.
     * @param event The event.
     */
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        pluginStop();
    }
}
