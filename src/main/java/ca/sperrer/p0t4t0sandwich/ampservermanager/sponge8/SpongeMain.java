package ca.sperrer.p0t4t0sandwich.ampservermanager.sponge8;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.LinearComponents;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The main class of your Sponge plugin.
 *
 * <p>All methods are optional -- some common event registrations are included as a jumping-off point.</p>
 */
@Plugin("ampservermanager")
public class SpongeMain {

    private final PluginContainer container;
    private final Logger logger;

    @Inject
    SpongeMain(final PluginContainer container, final Logger logger) {
        this.container = container;
        this.logger = logger;
    }

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;
    public static YamlDocument config;
    public static SpongeAMPServerManager ampServerManager;

    // Singleton instance
    private static SpongeMain instance;
    public static SpongeMain getInstance() {
        return instance;
    }
    public Logger getLogger() {
        return logger;
    }

    @Listener
    public void onConstructPlugin(final ConstructPluginEvent event) {
        // Config
        try {
            config = YamlDocument.create(defaultConfig.toFile());
            config.reload();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Singleton instance
        instance = this;

        // Start AMPAPAI Server Manager
        ampServerManager = new SpongeAMPServerManager(this, config, getLogger());

        Task.builder().execute(
                () -> ampServerManager.start()
        );

        getLogger().info("Constructing AMPServerManager");
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        // Register a simple command
        // When possible, all commands should be registered within a command register event
        final Parameter.Value<String> nameParam = Parameter.string().key("name").build();
        event.register(this.container, Command.builder()
                .addParameter(nameParam)
                .permission("AMPServerManager.command.greet")
                .executor(ctx -> {
                    final String name = ctx.requireOne(nameParam);
                    ctx.sendMessage(Identity.nil(), LinearComponents.linear(
                            NamedTextColor.AQUA,
                            Component.text("Hello "),
                            Component.text(name, Style.style(TextDecoration.BOLD)),
                            Component.text("!")
                    ));

                    return CommandResult.success();
                })
                .build(), "greet", "wave");
    }
}
