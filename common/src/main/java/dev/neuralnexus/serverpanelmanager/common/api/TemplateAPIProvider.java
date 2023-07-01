package dev.neuralnexus.serverpanelmanager.common.api;

import dev.neuralnexus.template.common.Template;

/**
 * Template API Provider
 */
public class TemplateAPIProvider {
    private static Template instance = null;

    /**
     * Get the instance of Template
     * @return The instance of Template
     */
    public static Template get() {
        if (instance == null) {
            throw new NotLoadedException();
        }
        return instance;
    }

    /**
     * DO NOT USE THIS METHOD, IT IS FOR INTERNAL USE ONLY
     * @param instance: The instance of Template
     */
    public static void register(Template instance) {
        TemplateAPIProvider.instance = instance;
    }

    /**
     * DO NOT USE THIS METHOD, IT IS FOR INTERNAL USE ONLY
     */
    public static void unregister() {
        TemplateAPIProvider.instance = null;
    }

    /**
     * Throw this exception when the API hasn't loaded yet, or you don't have the Template plugin installed.
     */
    private static final class NotLoadedException extends IllegalStateException {
        private static final String MESSAGE = "The API hasn't loaded yet, or you don't have the Template plugin installed.";

        NotLoadedException() {
            super(MESSAGE);
        }
    }
}
