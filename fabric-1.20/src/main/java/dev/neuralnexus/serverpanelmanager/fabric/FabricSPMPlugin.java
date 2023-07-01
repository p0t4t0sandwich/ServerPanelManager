package dev.neuralnexus.serverpanelmanager.fabric;

import dev.neuralnexus.template.common.TemplatePlugin;
import dev.neuralnexus.template.fabric.events.server.FabricServerStartingEvent;
import dev.neuralnexus.template.fabric.events.server.FabricServerStoppedEvent;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.LoggerFactory;

/**
 * The Template Fabric plugin.
 */
public class FabricSPMPlugin implements DedicatedServerModInitializer, TemplatePlugin {
    /**
     * @inheritDoc
     */
    @Override
    public Object pluginLogger() {
        return LoggerFactory.getLogger("taterapi");
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
        FabricServerStartingEvent.EVENT.register(server -> {});
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
