package dev.neuralnexus.serverpanelmanager.common.api;

import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;

/**
 * ServerPanelManager API Provider
 */
public class ServerPanelManagerAPIProvider {
    private static ServerPanelManager instance = null;

    /**
     * Get the instance of ServerPanelManager
     * @return The instance of ServerPanelManager
     */
    public static ServerPanelManager get() {
        if (instance == null) {
            throw new NotLoadedException();
        }
        return instance;
    }

    /**
     * DO NOT USE THIS METHOD, IT IS FOR INTERNAL USE ONLY
     * @param instance: The instance of ServerPanelManager
     */
    public static void register(ServerPanelManager instance) {
        ServerPanelManagerAPIProvider.instance = instance;
    }

    /**
     * DO NOT USE THIS METHOD, IT IS FOR INTERNAL USE ONLY
     */
    public static void unregister() {
        ServerPanelManagerAPIProvider.instance = null;
    }

    /**
     * Throw this exception when the API hasn't loaded yet, or you don't have the ServerPanelManager plugin installed.
     */
    private static final class NotLoadedException extends IllegalStateException {
        private static final String MESSAGE = "The API hasn't loaded yet, or you don't have the ServerPanelManager plugin installed.";

        NotLoadedException() {
            super(MESSAGE);
        }
    }
}
