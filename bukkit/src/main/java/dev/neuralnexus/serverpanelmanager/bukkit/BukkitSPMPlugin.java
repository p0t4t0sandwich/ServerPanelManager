package dev.neuralnexus.serverpanelmanager.bukkit;

import dev.neuralnexus.template.bukkit.commands.BukkitTemplateCommand;
import dev.neuralnexus.template.bukkit.listeners.BukkitPlayerLoginListener;
import dev.neuralnexus.template.common.TemplatePlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static dev.neuralnexus.template.common.Utils.getBukkitServerType;

/**
 * The Template Bukkit plugin.
 */
public class BukkitSPMPlugin extends JavaPlugin implements TemplatePlugin {
    /**
     * @inheritDoc
     */
    @Override
    public Object pluginLogger() {
        return getLogger();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String pluginConfigPath() {
        return "plugins";
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getServerType() {
        return getBukkitServerType();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerHooks() {}

    /**
     * @inheritDoc
     */
    @Override
    public void registerEventListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BukkitPlayerLoginListener(), this);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void registerCommands() {
        getCommand("template").setExecutor(new BukkitTemplateCommand());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onEnable() {
        pluginStart();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void onDisable() {
        pluginStop();
    }
}
