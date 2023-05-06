package ca.sperrer.p0t4t0sandwich.panelservermanager.bungee;

import ca.sperrer.p0t4t0sandwich.panelservermanager.common.PanelServerManager;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {
    public PanelServerManager panelServerManager;

    // Get server type
    public String getServerType() {
        return "BungeeCord";
    }

    // Singleton instance
    private static BungeeMain instance;
    public static BungeeMain getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        // Singleton instance
        instance = this;

        getLogger().info("Panel Server Manager is running on " + getServerType() + ".");

        // Start Panel Server Manager
        panelServerManager = new PanelServerManager("plugins", getLogger());
        panelServerManager.start();

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new BungeePanelCommands());

        // Plugin enable message
        getLogger().info("Panel Server Manager has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin disable message
        getLogger().info("Panel Server Manager has been disabled!");
    }
}
