package ca.sperrer.p0t4t0sandwich.panelservermanager.fabric;

import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.PanelServerManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricMain implements ModInitializer {
    // Logger
    public final Logger logger = LoggerFactory.getLogger("panelservermanager");
    public PanelServerManager panelServerManager;

    // Get server type
    public String getServerType() {
        return "Fabric";
    }

    // Singleton instance
    private static FabricMain instance;
    public static FabricMain getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        // Singleton instance
        instance = this;

        logger.info("[PanelServerManager]: Panel Server Manager is running on " + getServerType() + ".");

        // Start Panel Server Manager
        panelServerManager = new PanelServerManager("config", logger);
        panelServerManager.start();

        // Register commands
        CommandRegistrationCallback.EVENT.register(FabricPanelCommands::register);

        // Mod enable message
        logger.info("[PanelServerManager]: Panel Server Manager has been enabled!");
    }
}
