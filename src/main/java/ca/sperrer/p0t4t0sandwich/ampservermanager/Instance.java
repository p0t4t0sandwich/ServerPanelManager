package ca.sperrer.p0t4t0sandwich.ampservermanager;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Instance {
    public String host;
    public String username;
    public String password;
    public boolean isADS;

    public String name;
    public String id;
    public AMPAPIHandler API;

    public Instance(String host, String username, String password, boolean isADS, String name, String id, AMPAPIHandler API) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.isADS = isADS;
        this.name = name;
        this.id = id;
        this.API = API;
    }

    public boolean APILogin() {
        if (!isADS) {
            API = new AMPAPIHandler(host, username, password, "", "");
        } else {
            AMPAPIHandler ADS = new AMPAPIHandler(host, username, password, "", "");
            ADS.Login();

            if (name != null) {
                // Get instance ID
                if (id == null) {
                    // Loop through the targets
                    for (Map<String,Object> target : (ArrayList<Map<String,Object>>) ADS.ADSModule_GetInstances().get("result")) {

                        // Loop through the target instances
                        for (Map<String, Object> instance : (ArrayList<Map<String, Object>>) target.get("AvailableInstances")) {

                            // Grab the instance id
                            String instanceName = (String) instance.get("InstanceName");
                            if (instanceName.equals(name)) {
                                id = (String) instance.get("InstanceID");

                                // Save the id to the config
                                YamlDocument config = AMPServerManager.config;
                                config.set("servers." + name + ".id", id);
                                try {
                                    config.save();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }

                            // Break if the instance id is found
                            if (id != null) {
                                break;
                            }
                        }
                    }
                }
                API = ADS.InstanceLogin(id);
            }
        }
        if (API != null) {
            Map<?, ?> status = API.Login();
            return status.get("success").equals(true);
        } else {
            return false;
        }
    }

    // General Run Method
    public Map<String, Object> runMethod(Function<String[], Map<String, Object>> method) {
        try {
            return method.apply(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Start Server
    public void startServer() {
        runMethod((args) -> API.Core_Start());
    }

    // Stop Server
    public void stopServer() {
        runMethod((args) -> API.Core_Stop());
    }

    // Restart Server
    public void restartServer() {
        runMethod((args) -> API.Core_Restart());
    }

    // Kill Server
    public void killServer() {
        runMethod((args) -> API.Core_Kill());
    }

    // Sleep Server
    public void sleepServer() {
        runMethod((args) -> API.Core_Sleep());
    }

    // Send Command
    public void sendCommand(String command) {
        runMethod((args) -> API.Core_SendConsoleMessage(command));
    }

    // Get Status
    public Map<String, Object> getStatus() {
        return runMethod((args) -> API.Core_GetStatus());
    }

    // Parse Status
    public Map<String, Object> parseStatus(Map<String, Object> status) {
        if (status == null) {
            return null;
        }

        Map<String, Object> newStatus = new HashMap<>();

        Map<String, Object> Metrics = new HashMap<>();
        if (status.containsKey("Metrics")) {
            Metrics = (Map<String, Object>) status.get("Metrics");
            double CPU = (double) ((Map<String, Object>) Metrics.get("CPU Usage")).get("Percent");
            newStatus.put("CPU", CPU);
        }

        if (Metrics.containsKey("Memory Usage")) {
            Map<String, Object> Memory = (Map<String, Object>) Metrics.get("Memory Usage");
            int MemoryValue = (int) Math.round((double) Memory.get("RawValue"));
            int MemoryMax = (int) Math.round((double) Memory.get("MaxValue"));
            newStatus.put("MemoryValue", MemoryValue);
            newStatus.put("MemoryMax", MemoryMax);
        }

        if (Metrics.containsKey("Active Users")) {
            Map<String, Object> Players = (Map<String, Object>) Metrics.get("Active Users");
            int PlayersValue = (int) Math.round((double) Players.get("RawValue"));
            int PlayersMax = (int) Math.round((double) Players.get("MaxValue"));
            newStatus.put("PlayersValue", PlayersValue);
            newStatus.put("PlayersMax", PlayersMax);
        }

        if (Metrics.containsKey("TPS")) {
            Map<String, Object> TPS = (Map<String, Object>) Metrics.get("TPS");
            double TPSValue = (double) TPS.get("RawValue");
            newStatus.put("TPSValue", TPSValue);
        }

        // Convert state to string
        if (status.containsKey("State")) {
            int state = (int) Math.round((double) status.get("State"));
            switch (state) {
                case -1:
                    newStatus.put("State", "Undefined");
                    break;
                case 0:
                    newStatus.put("State", "Stopped");
                    break;
                case 5:
                    newStatus.put("State", "PreStart");
                    break;
                case 7:
                    newStatus.put("State", "Configuring");
                    break;
                case 10:
                    newStatus.put("State", "Starting");
                    break;
                case 20:
                    newStatus.put("State", "Ready");
                    break;
                case 30:
                    newStatus.put("State", "Restarting");
                    break;
                case 40:
                    newStatus.put("State", "Stopping");
                    break;
                case 45:
                    newStatus.put("State", "PreparingForSleep");
                    break;
                case 50:
                    newStatus.put("State", "Sleeping");
                    break;
                case 60:
                    newStatus.put("State", "Waiting");
                    break;
                case 70:
                    newStatus.put("State", "Installing");
                    break;
                case 75:
                    newStatus.put("State", "Updating");
                    break;
                case 80:
                    newStatus.put("State", "AwaitingUserInput");
                    break;
                case 100:
                    newStatus.put("State", "Failed");
                    break;
                case 200:
                    newStatus.put("State", "Suspended");
                    break;
                case 250:
                    newStatus.put("State", "Maintenance");
                    break;
                case 999:
                    newStatus.put("State", "Indeterminate");
                    break;
            }
        }

        return newStatus;
    }

    // Backup Server
    public void backupServer(String backupTitle, String backupDescription, boolean isSticky) {
        runMethod((args) -> API.LocalFileBackupPlugin_TakeBackup(backupTitle, backupDescription, isSticky));
    }
}