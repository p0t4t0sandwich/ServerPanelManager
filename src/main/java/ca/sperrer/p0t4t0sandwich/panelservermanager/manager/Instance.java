package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Instance {
    /**
     * Properties of the Instance class.
     * host: The host URL of the AMP instance
     * username: The AMP username
     * password: The AMP password
     * isADS: Whether the AMP instance is an ADS instance
     * serverName: The name that the server is referred to
     * name: The InstanceName of the AMP instance
     * id: The InstanceID of the AMP instance
     * API: The AMPAPIHandler object for the instance
     */
    private final String host;
    private final String username;
    private final String password;
    private final boolean isADS;
    public final String serverName;
    public final String name;
    private String id;
    private AMPAPIHandler API;

    /**
     * Constructor for the Instance class.
     * @param host: The host URL of the AMP instance
     * @param username: The AMP username
     * @param password: The AMP password
     * @param isADS: Whether the AMP instance is an ADS instance
     * @param serverName: The name that the server is referred to
     * @param instanceName: The InstanceName of the AMP instance
     * @param instanceId: The InstanceID of the AMP instance
     * @return Instance object
     */
    public Instance(String host, String username, String password, boolean isADS, String serverName, String instanceName, String instanceId) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.isADS = isADS;
        this.serverName = serverName;
        this.name = instanceName;
        this.id = instanceId;
    }

    /**
     * Initialize the API object and login to the AMP instance.
     * If the instance is an ADS instance, get the instance id and proxy the login through the ADS instance.
     * Else, login directly to the AMP instance.
     * @return Whether the login was successful
     */
    public boolean APILogin(AMPAPIHandler ADS) {
        if (!isADS) {
            API = new AMPAPIHandler(host, username, password, "", "");
        } else {
//            AMPAPIHandler ADS = new AMPAPIHandler(host, username, password, "", "");
//            ADS.Login();

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
                                if (instance.containsKey("InstanceID")) {
                                    id = ((String) instance.get("InstanceID")).split("-")[0];
                                }

                                // Save the id to the config
                                PanelServerManager.getInstance().addInstanceID(serverName, id);
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

    /**
     * Acts as a wrapper for the Instance object to abstract the API.
     * @param method: The method to run
     * @return The result of the method
     */
    public Map<String, Object> runMethod(Function<String[], Map<String, Object>> method) {
        try {
            return method.apply(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Abstraction for API.Core.Start
     */
    public void startServer() {
        runMethod((args) -> API.Core_Start());
    }

    /**
     * Abstraction for API.Core.Stop
     */
    public void stopServer() {
        runMethod((args) -> API.Core_Stop());
    }

    /**
     * Abstraction for API.Core.Restart
     */
    public void restartServer() {
        runMethod((args) -> API.Core_Restart());
    }

    /**
     * Abstraction for API.Core.Kill
     */
    public void killServer() {
        runMethod((args) -> API.Core_Kill());
    }

    /**
     * Abstraction for API.Core.Sleep
     */
    public void sleepServer() {
        runMethod((args) -> API.Core_Sleep());
    }

    /**
     * Abstraction for API.Core.SendConsoleMessage
     * @param message: The message/command to send
     */
    public void sendCommand(String message) {
        runMethod((args) -> API.Core_SendConsoleMessage(message));
    }

    /**
     * Abstraction for API.Core.GetStatus
     * @return The status object from the API
     */
    public Map<String, Object> getStatus() {
        return runMethod((args) -> API.Core_GetStatus());
    }

    /**
     * A parser for this.getStatus()
     * @param status: The status object from the API
     * @return A parsed status object
     */
    public Map<String, Object> parseStatus(Map<String, Object> status) {
        if (status == null) {
            return null;
        }

        Map<String, Object> newStatus = new HashMap<>();

        // Check if Metrics is present and parse it
        if (status.containsKey("Metrics")) {
            Map<String, Object> Metrics = (Map<String, Object>) status.get("Metrics");
            double CPU = (double) ((Map<String, Object>) Metrics.get("CPU Usage")).get("Percent");
            newStatus.put("CPU", CPU);

            // Check if the Metrics contains Memory Usage and add it to the newStatus object
            if (Metrics.containsKey("Memory Usage")) {
                Map<String, Object> Memory = (Map<String, Object>) Metrics.get("Memory Usage");
                int MemoryValue = (int) Math.round((double) Memory.get("RawValue"));
                int MemoryMax = (int) Math.round((double) Memory.get("MaxValue"));
                newStatus.put("MemoryValue", MemoryValue);
                newStatus.put("MemoryMax", MemoryMax);
            }

            // Check if the Metrics contains Active Users and add it to the newStatus object
            if (Metrics.containsKey("Active Users")) {
                Map<String, Object> Players = (Map<String, Object>) Metrics.get("Active Users");
                int PlayersValue = (int) Math.round((double) Players.get("RawValue"));
                int PlayersMax = (int) Math.round((double) Players.get("MaxValue"));
                newStatus.put("PlayersValue", PlayersValue);
                newStatus.put("PlayersMax", PlayersMax);
            }

            // Check if the Metrics contains TPS and add it to the newStatus object
            if (Metrics.containsKey("TPS")) {
                Map<String, Object> TPS = (Map<String, Object>) Metrics.get("TPS");
                double TPSValue = (double) TPS.get("RawValue");
                newStatus.put("TPSValue", TPSValue);
            }
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

    /**
     * Abstraction for API.LocalFileBackupPlugin.TakeBackup
     * @param backupTitle: The title of the backup
     * @param backupDescription: The description of the backup
     * @param isSticky: Whether the backup is sticky or not
     */
    public void backupServer(String backupTitle, String backupDescription, boolean isSticky) {
        runMethod((args) -> API.LocalFileBackupPlugin_TakeBackup(backupTitle, backupDescription, isSticky));
    }

    /**
     * Abstraction for API.Core.GetUserList
     * @return The player list object from the API
     */
    public Map<String, Object> getPlayerList() {
        return runMethod((args) -> (Map<String, Object>) API.Core_GetUserList().get("result"));
    }

    /**
     * A parser for this.getPlayerList()
     * @param playerList: The player list object from the API
     * @return A parsed player list object
     */
    public List<String> parsePlayerList(Map<String, Object> playerList) {
        if (playerList == null) {
            return null;
        }
        List<String> players = new ArrayList<>();
        for (Map.Entry<String, Object> entry : playerList.entrySet()) {
            players.add((String) entry.getValue());
        }
        return players;
    }
}