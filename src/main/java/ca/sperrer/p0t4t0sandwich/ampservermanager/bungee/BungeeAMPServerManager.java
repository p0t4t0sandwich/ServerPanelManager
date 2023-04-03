package ca.sperrer.p0t4t0sandwich.ampservermanager.bungee;


import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Map;

class BungeeAMPServerManager extends AMPServerManager<Plugin> {
    public BungeeAMPServerManager(Plugin plugin, YamlDocument config) {
        super(plugin, config);
    }

    @Override
    public void start() {
        ADS = new AMPAPIHandler(host, username, password, "", "");
        ADS.Login();

        // Get instances
        Map<String, ?> serverConfig = (Map<String, ?>) config.getBlock("servers").getStoredValue();
        for (Map.Entry<String, ?> entry: serverConfig.entrySet()) {
            // Get instance name and id
            String serverName = entry.getKey();
            String name = config.getString("servers." + serverName + ".name");
            String id = config.getString("servers." + serverName + ".id");

            Instance instance = new Instance(name, id, null);
            boolean status = instanceLogin(instance);
            if (status) {
                plugin.getLogger().info("Instance " + instance.name + " is online!");
            } else {
                plugin.getLogger().info("Instance " + instance.name + " is offline!");
            }
        }
    }
}