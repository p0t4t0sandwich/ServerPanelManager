package ca.sperrer.p0t4t0sandwich.ampservermanager.spigot;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

class SpigotAMPServerManager extends AMPServerManager<JavaPlugin> {

    // Constructor
    public SpigotAMPServerManager(JavaPlugin plugin, YamlDocument config)  {
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