package dev.neuralnexus.serverpanelmanager.common;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * The TaterAPI plugin interface.
 */
public interface ServerPanelManagerPlugin {
    /**
     * Gets the config path.
     */
    String pluginConfigPath();

    /**
     * Use whatever logger is being used.
     * @param message The message to log
     */
    static void useLogger(String message) {
        System.out.println(message);
    }

    /**
     * Gets the server type.
     * @return The server type
     */
    default String getServerType() {
        return "unknown";
    }

    /**
     * Gets the server version.
     * @return The server version
     */
    default String getServerVersion() {
        return "unknown";
    }

    /**
     * Register hooks.
     */
    void registerHooks();


    /**
     * Registers event listeners.
     */
    void registerEventListeners();

    /**
     * Registers commands.
     */
    void registerCommands();

    /**
     * Starts the ServerPanelManager plugin.
     */
    default void pluginStart() {
        runTaskAsync(() -> {
            try {
                useLogger("ServerPanelManager is running on " + getServerType() + " " + getServerVersion() + "!");

                // Start the TaterAPI
                ServerPanelManager.start(pluginConfigPath());

                // Register hooks
                registerHooks();

                // Register event listeners
                registerEventListeners();

                // Register commands
                registerCommands();

                useLogger("ServerPanelManager has been enabled!");

            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }

    /**
     * Stops the TaterAPI plugin.
     */
    default void pluginStop() {
        runTaskAsync(() -> {
            try {
                ServerPanelManager.stop();
                useLogger("ServerPanelManager has been disabled!");
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
