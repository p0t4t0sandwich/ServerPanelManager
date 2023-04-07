package ca.sperrer.p0t4t0sandwich.ampservermanager.fabric;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class FabricMain implements ModInitializer,ClientModInitializer {
    // Logger
    public static final Logger logger = LoggerFactory.getLogger("ampservermanager");
    public AMPServerManager ampServerManager;

    // Singleton instance
    private static FabricMain instance;
    public static FabricMain getInstance() {
        return instance;
    }

    public void init() {
        // Singleton instance
        instance = this;

        // Start AMPAPAI Server Manager
        ampServerManager = new AMPServerManager("config", logger);
        (new Thread(() -> ampServerManager.start())).start();

        // Register commands
        CommandRegistrationCallback.EVENT.register(FabricAMPCommands::register);

        // Mod enable message
        logger.info("[AMPServerManager]: AMPAPAI Server Manager has been enabled!");
    }

    @Override
    public void onInitialize() {
        init();
    }

    @Override
    public void onInitializeClient() {
        init();
    }
}
