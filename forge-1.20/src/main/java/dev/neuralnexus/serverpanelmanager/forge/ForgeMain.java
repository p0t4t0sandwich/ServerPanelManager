package dev.neuralnexus.serverpanelmanager.forge;

import dev.neuralnexus.serverpanelmanager.common.PanelServerManager;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ForgeMain.MODID)
public class ForgeMain {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "panelservermanager";

    // Directly reference a slf4j logger
    public static final Logger logger = LogUtils.getLogger();
    public PanelServerManager panelServerManager;

    // Get server type
    public String getServerType() {
        return "Forge";
    }

    // Singleton instance
    private static ForgeMain instance;

    public static ForgeMain getInstance() {
        return instance;
    }


    public ForgeMain() {
        // Singleton instance
        instance = this;

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ForgeCommandEvent.class);
    }

    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        logger.info("[PanelServerManager]: Panel Server Manager is running on " + getServerType() + ".");

        // Start Panel Server Manager
        panelServerManager = new PanelServerManager("config", logger);
        panelServerManager.start();

        // Mod enable message
        logger.info("[PanelServerManager]: Panel Server Manager has been enabled!");

        // Mod enable message
        logger.info("[PanelServerManager]: Panel Server Manager has been enabled!");
    }
}
