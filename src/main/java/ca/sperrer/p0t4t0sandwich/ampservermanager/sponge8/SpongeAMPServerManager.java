package ca.sperrer.p0t4t0sandwich.ampservermanager.sponge8;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.apache.logging.log4j.Logger;

import java.util.Map;

class SpongeAMPServerManager extends AMPServerManager<SpongeMain> {
    private Logger logger;
    // Constructor
    public SpongeAMPServerManager(SpongeMain plugin, YamlDocument config, Logger logger) {
        super(plugin, config);
        this.logger = logger;

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
                logger.info("Instance " + instance.name + " is online!");
            } else {
                logger.info("Instance " + instance.name + " is offline!");
            }
        }
    }

}