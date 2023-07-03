package dev.neuralnexus.serverpanelmanager.fabric;

import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import dev.neuralnexus.serverpanelmanager.fabric.events.server.FabricServerStartingEvent;
import dev.neuralnexus.serverpanelmanager.fabric.events.server.FabricServerStoppedEvent;
import net.fabricmc.api.DedicatedServerModInitializer;
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
        FabricServerStartingEvent.EVENT.register(server -> {
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
    public void registerEventListeners() {}

    /**
     * @inheritDoc
     */
    @Override
    public void registerCommands() {}

    /**
     * @inheritDoc
     */
    @Override
    public void onInitializeServer() {
        pluginStart();
        FabricServerStoppedEvent.EVENT.register(server -> pluginStop());
    }
}
