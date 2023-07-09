package dev.neuralnexus.serverpanelmanager.common.cubecodersamp;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import dev.neuralnexus.serverpanelmanager.common.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AMPServer extends Server {
    /**
     * Properties of the AMPServer class
     * serverName: The name of the server
     * instanceName: The InstanceName of the AMP instance
     * instanceId: The InstanceID of the AMP instance
     * API: The AMPAPIHandler object for the instance
     * loginResult: The result of the login method
     *             (null if the login was not successful)
     */
    private final String instanceName;
    private final String instanceId;
    private final AMPAPIHandler API;
    private Map<?, ?> loginResult;

    /**
     * Constructor for the AMPServer class
     * @param serverName The name that the server is referred to
     * @param instanceName The InstanceName of the AMP instance
     * @param instanceId The InstanceID of the AMP instance
     * @param API The AMPAPIHandler object for the instance
     */
    public AMPServer(String serverName, String panelName, String instanceName, String instanceId, AMPAPIHandler API) {
        super(serverName, panelName);
        this.instanceName = instanceName;
        this.instanceId = instanceId;
        this.API = API;
        this.loginResult = API.Login();
    }

    /**
     * Get InstanceName
     * @return The InstanceName of the AMP instance
     */
    public String getInstanceName() {
        return instanceName;
    }

    /**
     * Get InstanceID
     * @return The InstanceID of the AMP instance
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isOnline() {
        return (loginResult != null && (boolean) loginResult.get("success"));
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean reLog() {
        this.loginResult = API.Login();
        return (loginResult != null && (boolean) loginResult.get("success"));
    }

    /**
     * Acts as a wrapper for the Instance object to abstract the API.
     * @param method The method to run
     * @return The result of the method
     */
    public Map<?, ?> runMethod(Function<String[], Map<?, ?>> method) {
        try {
            return method.apply(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void startServer() {
        runMethod((args) -> API.Core_Start());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void stopServer() {
        runMethod((args) -> API.Core_Stop());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void restartServer() {
        runMethod((args) -> API.Core_Restart());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void killServer() {
        runMethod((args) -> API.Core_Kill());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void sendCommand(String message) {
        runMethod((args) -> API.Core_SendConsoleMessage(message));
    }

    /**
     * A parser for this.getStatus()
     * @param status The status object from the API
     * @return A parsed status object
     */
    public Map<String, Object> parseStatus(Map<?, ?> status) {
        if (status == null) {
            return null;
        }

        Map<String, Object> newStatus = new HashMap<>();

        // Check if Metrics is present and parse it
        if (status.containsKey("Metrics")) {
            Map<?, ?> Metrics = (Map<?, ?>) status.get("Metrics");
            double CPU = (double) ((Map<?, ?>) Metrics.get("CPU Usage")).get("Percent");
            newStatus.put("CPU", CPU);

            // Check if the Metrics contains Memory Usage and add it to the newStatus object
            if (Metrics.containsKey("Memory Usage")) {
                Map<?, ?> Memory = (Map<?, ?>) Metrics.get("Memory Usage");
                int MemoryValue = (int) Math.round((double) Memory.get("RawValue"));
                int MemoryMax = (int) Math.round((double) Memory.get("MaxValue"));
                newStatus.put("MemoryValue", MemoryValue);
                newStatus.put("MemoryMax", MemoryMax);
            }

            // Check if the Metrics contains Active Users and add it to the newStatus object
            if (Metrics.containsKey("Active Users")) {
                Map<?, ?> Players = (Map<?, ?>) Metrics.get("Active Users");
                int PlayersValue = (int) Math.round((double) Players.get("RawValue"));
                int PlayersMax = (int) Math.round((double) Players.get("MaxValue"));
                newStatus.put("PlayersValue", PlayersValue);
                newStatus.put("PlayersMax", PlayersMax);
            }

            // Check if the Metrics contains TPS and add it to the newStatus object
            if (Metrics.containsKey("TPS")) {
                Map<?, ?> TPS = (Map<?, ?>) Metrics.get("TPS");
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
     * @inheritDoc
     */
    @Override
    public Map<String, Object> getStatus() {
        Map<?, ?> status = runMethod((args) -> API.Core_GetStatus());
        return parseStatus(status);
    }

    /**
     * Abstraction for API.Core.Sleep
     */
    public void sleepServer() {
        runMethod((args) -> API.Core_Sleep());
    }

    /**
     * Abstraction for API.LocalFileBackupPlugin.TakeBackup
     * @param backupTitle The title of the backup
     * @param backupDescription The description of the backup
     * @param isSticky Whether the backup is sticky or not
     */
    public void backupServer(String backupTitle, String backupDescription, boolean isSticky) {
        runMethod((args) -> API.LocalFileBackupPlugin_TakeBackup(backupTitle, backupDescription, isSticky));
    }

    /**
     * A parser for this.getPlayerList()
     * @param playerList The player list object from the API
     * @return A parsed player list object
     */
    public List<String> parsePlayerList(Map<?, ?> playerList) {
        if (playerList == null) {
            return null;
        }
        List<String> players = new ArrayList<>();
        for (HashMap.Entry<?, ?> entry : playerList.entrySet()) {
            players.add((String) entry.getValue());
        }
        return players;
    }

    /**
     * Abstraction for API.Core.GetUserList
     * @return The player list object from the API
     */
    public List<String> getPlayerList() {
        Map<?, ?> playerList = runMethod((args) -> (Map<?, ?>) API.Core_GetUserList().get("result"));
        return parsePlayerList(playerList);
    }

    /**
     * Get a Map of schedule triggers
     * @return A Map of schedule triggers
     */
    public Map<String, String> getScheduleTriggers() {
        // Get the raw triggers
        Map<?, ?> scheduleTriggers = runMethod((args) -> (Map<?, ?>) API.Core_GetScheduleData().get("result"));
        ArrayList<Map<String, Object>> rawTriggers = (ArrayList<Map<String, Object>>) scheduleTriggers.get("PopulatedTriggers");

        // Convert the raw triggers into a Map
        Map<String, String> triggers = new HashMap<>();
        for (Map<String, Object> trigger : rawTriggers) {
            triggers.put((String) trigger.get("Description"), (String) trigger.get("Id"));
        }

        return triggers;
    }

    /**
     * Run a schedule trigger
     * @param triggerId The ID of the trigger to run
     * @return The result of the trigger
     */
    public Map<?, ?> runScheduleTrigger(String triggerId) {
        API.Core_SetTriggerEnabled(triggerId, true);
        Map result = runMethod((args) -> API.Core_RunEventTriggerImmediately(triggerId));
        API.Core_SetTriggerEnabled(triggerId, false);
        return result;
    }
}
