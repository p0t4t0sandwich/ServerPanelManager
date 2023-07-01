package dev.neuralnexus.serverpanelmanager.common;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.neuralnexus.template.common.api.TemplateAPIProvider;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Template {
    /**
     * Properties of the Template class.
     * instance: The singleton instance of the Template class
     * config: The config file
     * logger: The logger
     * STARTED: Whether the PanelServerManager has been started
     */
    private static final Template instance = new Template();
    private static YamlDocument config;
    private static Object logger;
    private static String configPath;
    private static boolean STARTED = false;

    /**
     * Constructor for the Template class.
     */
    public Template() {}

    /**
     * Getter for the singleton instance of the Template class.
     * @return The singleton instance
     */
    public static Template getInstance() {
        return instance;
    }

    /**
     * Use whatever logger is being used.
     * @param message The message to log
     */
    public static void useLogger(String message) {
        if (logger instanceof java.util.logging.Logger) {
            ((java.util.logging.Logger) logger).info(message);
        } else if (logger instanceof org.slf4j.Logger) {
            ((org.slf4j.Logger) logger).info(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Start Template
     * @param configPath The path to the config file
     * @param logger The logger
     */
    public static void start(String configPath, Object logger) {
        Template.configPath = configPath;
        Template.logger = logger;

        // Config
        try {
            config = YamlDocument.create(new File("." + File.separator + configPath + File.separator + "TaterAPI", "config.yml"),
                    Objects.requireNonNull(Template.class.getClassLoader().getResourceAsStream("config.yml"))
            );
            config.reload();
        } catch (IOException | NullPointerException e) {
            useLogger("Failed to load config.yml!\n" + e.getMessage());
            e.printStackTrace();
        }

        if (STARTED) {
            useLogger("TaterAPI has already started!");
            return;
        }
        STARTED = true;

        useLogger("TaterAPI has been started!");
        TemplateAPIProvider.register(instance);
    }

    /**
     * Start TaterAPI
     */
    public static void start() {
        start(configPath, logger);
    }

    /**
     * Stop TaterAPI
     */
    public static void stop() {
        if (!STARTED) {
            useLogger("TaterAPI has already stopped!");
            return;
        }
        STARTED = false;

        useLogger("TaterAPI has been stopped!");
        TemplateAPIProvider.unregister();
    }
}
