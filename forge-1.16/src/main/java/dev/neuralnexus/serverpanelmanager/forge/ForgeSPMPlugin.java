package dev.neuralnexus.serverpanelmanager.forge;

import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManagerPlugin;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import dev.neuralnexus.serverpanelmanager.forge.commands.ForgeSPMCommand;
import dev.neuralnexus.serverpanelmanager.forge.listeners.player.ForgePlayerListener;
import dev.neuralnexus.serverpanelmanager.forge.listeners.server.ForgeServerListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

/**
 * The ServerPanelManager Forge plugin.
 */
@Mod("serverpanelmanager")
public class ForgeSPMPlugin implements ServerPanelManagerPlugin {
    Logger logger = LogManager.getLogger("serverpanelmanager");

    /**
     * Use whatever logger is being used.
     * @param message The message to log
     */
    public void useLogger(String message) {
        logger.info("[ServerPanelManager] " + message);
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
    public String getServerVersion() {
        // Reflect to get the Minecraft and Forge versions from FMLLoader
        String mcVersion = null;
        String forgeVersion = null;

        try {
            Field mcVersionField = FMLLoader.class.getDeclaredField("mcVersion");
            mcVersionField.setAccessible(true);
            mcVersion = (String) mcVersionField.get(null);
            Field forgeVersionField = FMLLoader.class.getDeclaredField("forgeVersion");
            forgeVersionField.setAccessible(true);
            forgeVersion = (String) forgeVersionField.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        return mcVersion + "-" + forgeVersion;
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
    public void onServerStarting(FMLServerStartingEvent event) {
        // Register LuckPerms hook
        if (ModList.get().isLoaded("luckperms")) {
            useLogger("LuckPerms detected, enabling LuckPerms hook.");
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
        MinecraftForge.EVENT_BUS.register(new ForgePlayerListener());

        // Register server event listeners
        MinecraftForge.EVENT_BUS.register(new ForgeServerListener());

        // Register commands
        MinecraftForge.EVENT_BUS.register(ForgeSPMCommand.class);
        pluginStart();
    }

    /**
     * Called when the server is stopping.
     * @param event The event.
     */
    @SubscribeEvent
    public void onServerStopped(FMLServerStoppedEvent event) {
        pluginStop();
    }
}
