package dev.neuralnexus.serverpanelmanager.fabric;

import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import dev.neuralnexus.serverpanelmanager.common.listneners.player.*;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartedListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStartingListener;
import dev.neuralnexus.serverpanelmanager.common.listneners.server.SPMServerStoppedListener;
import dev.neuralnexus.serverpanelmanager.fabric.commands.FabricSPMCommand;
import dev.neuralnexus.serverpanelmanager.fabric.events.player.FabricPlayerEvents;
import dev.neuralnexus.serverpanelmanager.fabric.player.FabricPlayer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

/**
 * The ServerPanelManager Fabric plugin.
 */
public class FabricSPMPlugin implements DedicatedServerModInitializer, ServerPanelManagerPlugin {
    /**
     * @inheritDoc
     */
    @Override
    public Object pluginLogger() {
        return LoggerFactory.getLogger("serverpanelmanager");
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
        return "Fabric";
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerHooks() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            if (FabricLoader.getInstance().isModLoaded("luckperms")) {
                useLogger("[ServerPanelManager] LuckPerms detected, enabling LuckPerms hook.");
                ServerPanelManager.addHook(new LuckPermsHook());
            }
        });
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerEventListeners() {
        // Register player events
        FabricPlayerEvents.ADVANCEMENT.register((player, advancement) -> SPMPlayerAdvancementListener.onPlayerAdvancement(new FabricPlayer(player), advancement.getId().toString()));
        FabricPlayerEvents.DEATH.register((player, source) -> SPMPlayerDeathListener.onPlayerDeath(new FabricPlayer(player), source.getDeathMessage(player).getString()));
        FabricPlayerEvents.MESSAGE.register((player, message, isCancelled) -> SPMPlayerMessageListener.onPlayerMessage(new FabricPlayer(player), message, isCancelled));

        // Register Fabric API player events
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> SPMPlayerLoginListener.onPlayerLogin(new FabricPlayer(handler.player)));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> SPMPlayerLogoutListener.onPlayerLogout(new FabricPlayer(handler.player)));

        // Register Fabric API server events
        ServerLifecycleEvents.SERVER_STARTING.register(server -> SPMServerStartingListener.onServerStarting());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> SPMServerStartedListener.onServerStarted());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> SPMServerStoppedListener.onServerStopped());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register(FabricSPMCommand::register);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onInitializeServer() {
        pluginStart();
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> pluginStop());
    }
}
