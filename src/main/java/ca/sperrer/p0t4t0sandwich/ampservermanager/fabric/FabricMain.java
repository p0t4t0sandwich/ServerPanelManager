package ca.sperrer.p0t4t0sandwich.ampservermanager.fabric;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricMain implements ModInitializer {
    // Logger
    public final Logger logger = LoggerFactory.getLogger("ampservermanager");
    public AMPServerManager ampServerManager;

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

        logger.info("AMPAPAI Server Manager is running on " + getServerType() + ".");

        // Start AMPAPAI Server Manager
        ampServerManager = new AMPServerManager("config", logger);
        ampServerManager.start();

        // Register commands
        CommandRegistrationCallback.EVENT.register(FabricAMPCommands::register);

        // Mod enable message
        logger.info("[AMPServerManager]: AMPAPAI Server Manager has been enabled!");
    }
}
