package ca.sperrer.p0t4t0sandwich.panelservermanager.api;

import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.PanelServerManager;

public final class PanelServerManagerProvider {
    private static PanelServerManager instance = null;

    /**
     * Get the instance of AMPServerManager
     * @return The instance of AMPServerManager
     */
    public static PanelServerManager get() {
        if (instance == null) {
            throw new NotLoadedException();
        }
        return instance;
    }

    /**
     * DO NOT USE THIS METHOD, IT IS FOR INTERNAL USE ONLY
     * @param instance: The instance of AMPServerManager
     */
    public static void register(PanelServerManager instance) {
        PanelServerManagerProvider.instance = instance;
    }

    /**
     * Throw this exception when the API hasn't loaded yet, or you don't have the AMPServerManager plugin installed.
     */
    private static final class NotLoadedException extends IllegalStateException {
        private static final String MESSAGE = "The API hasn't loaded yet, or you don't have the AMPServerManager plugin installed.";

        NotLoadedException() {
            super(MESSAGE);
        }
    }
}
