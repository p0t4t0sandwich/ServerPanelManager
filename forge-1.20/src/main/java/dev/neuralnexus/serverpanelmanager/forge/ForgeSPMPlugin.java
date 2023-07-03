package dev.neuralnexus.serverpanelmanager.forge;

import com.mojang.logging.LogUtils;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import dev.neuralnexus.serverpanelmanager.forge.commands.ForgeSPMCommand;
import dev.neuralnexus.serverpanelmanager.forge.listeners.player.*;
import dev.neuralnexus.serverpanelmanager.forge.listeners.server.ForgeServerStartedListener;
import dev.neuralnexus.serverpanelmanager.forge.listeners.server.ForgeServerStartingListener;
import dev.neuralnexus.serverpanelmanager.forge.listeners.server.ForgeServerStoppedListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

/**
 * The ServerPanelManager Forge plugin.
 */
@Mod("serverpanelmanager")
public class ForgeSPMPlugin implements ServerPanelManagerPlugin {
    /**
     * @inheritDoc
     */
    @Override
    public Object pluginLogger() {
        return LogUtils.getLogger();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String pluginConfigPath() {
        return "config";
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getServerType() {
        return "Forge";
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerHooks() {}

    /**
     * Called when the server is starting.
     * @param event The event.
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Register LuckPerms hook
        if (ModList.get().isLoaded("luckperms")) {
            useLogger("[ServerPanelManager] LuckPerms detected, enabling LuckPerms hook.");
            ServerPanelManager.addHook(new LuckPermsHook());
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerEventListeners() {}

    /**
     * @inheritDoc
     */
    @Override
    public void registerCommands() {}

    /**
     * Called when the Forge mod is initializing.
     */
    public ForgeSPMPlugin() {
        // Register server starting/stopping events
        MinecraftForge.EVENT_BUS.register(this);

        // Register player event listeners
        MinecraftForge.EVENT_BUS.register(new ForgePlayerAdvancementListener());
        MinecraftForge.EVENT_BUS.register(new ForgePlayerDeathListener());
        MinecraftForge.EVENT_BUS.register(new ForgePlayerLoginListener());
        MinecraftForge.EVENT_BUS.register(new ForgePlayerLogoutListener());
        MinecraftForge.EVENT_BUS.register(new ForgePlayerMessageListener());

        // Register server event listeners
        MinecraftForge.EVENT_BUS.register(new ForgeServerStartedListener());
        MinecraftForge.EVENT_BUS.register(new ForgeServerStartingListener());
        MinecraftForge.EVENT_BUS.register(new ForgeServerStoppedListener());

        // Register commands
        MinecraftForge.EVENT_BUS.register(ForgeSPMCommand.class);
        pluginStart();
    }

    /**
     * Called when the server is stopping.
     * @param event The event.
     */
    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        pluginStop();
    }
}
