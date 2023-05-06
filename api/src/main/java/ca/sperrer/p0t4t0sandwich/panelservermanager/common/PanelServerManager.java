package ca.sperrer.p0t4t0sandwich.panelservermanager.common;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import ca.sperrer.p0t4t0sandwich.panelservermanager.api.PanelServerManagerProvider;
import ca.sperrer.p0t4t0sandwich.panelservermanager.common.cubecodersamp.AMPPanel;
import ca.sperrer.p0t4t0sandwich.panelservermanager.common.cubecodersamp.AMPServer;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ca.sperrer.p0t4t0sandwich.panelservermanager.common.Utils.runTaskAsync;

public class PanelServerManager {
    /**
     * Properties of the PanelServerManager class.
     * config: The config file
     * logger: The logger
     * panels: A HashMap of panels
     * servers: A HashMap of instances
     * groups: A HashMap of groups
     * singleton: The singleton instance of the PanelServerManager class
     * commandHandler: The command handler
     * STARTED: Whether the PanelServerManager has been started
     */
    private static YamlDocument config;
    private final Object logger;
    private static final HashMap<String, Panel> panels = new HashMap<>();
    private static final HashMap<String, Server> servers = new HashMap<>();
    private static final HashMap<String, Group> groups = new HashMap<>();
    private static PanelServerManager singleton = null;
    public final CommandHandler commandHandler = new CommandHandler(this);
    private boolean STARTED = false;

    /**
     * Constructor for the PanelServerManager class.
     * @param configPath The path to the config file
     * @param logger The logger
     */
    public PanelServerManager(String configPath, Object logger) {
        singleton = this;
        this.logger = logger;

        // Register the singleton instance in the PanelServerManagerProvider class
        PanelServerManagerProvider.register(this);

        // Config
        try {
            config = YamlDocument.create(new File("./" + configPath + "/PanelServerManager", "config.yml"),
                    Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("config.yml"))
            );
            config.reload();
        } catch (IOException e) {
            useLogger("Failed to load config.yml!\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Getter for the singleton instance of the PanelServerManager class.
     * @return The singleton instance
     */
    public static PanelServerManager getInstance() {
        return singleton;
    }

    /**
     * Use whatever logger is being used.
     * @param message The message to log
     */
    public void useLogger(String message) {
        if (logger instanceof java.util.logging.Logger) {
            ((java.util.logging.Logger) logger).info(message);
        } else if (logger instanceof org.slf4j.Logger) {
            ((org.slf4j.Logger) logger).info(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Start the PanelServerManager.
     */
    public void start() {
        if (STARTED) {
            useLogger("PanelServerManager is already started!");
            return;
        }
        runTaskAsync(() -> {
            STARTED = true;
            // Initialize Panels
            useLogger("Initializing panels...");
            initPanels();

            // Initialize servers
            useLogger("Initializing servers...");
            initServers();

            // Initialize groups
            useLogger("Initializing groups...");
            initGroups();
        });
    }

    /**
     * Initialize panels
     */
    private void initPanels() {
        HashMap<?, ?> panelConfig = (HashMap<?, ?>) config.getBlock("panels").getStoredValue();
        for (HashMap.Entry<?, ?> entry: panelConfig.entrySet()) {
            // Get panel name and type
            String panelName = (String) entry.getKey();
            String panelType = config.getString("panels." + panelName + ".type");
            Panel panel = null;

            switch (panelType) {
                case "cubecodersamp":
                    String host = config.getString("panels." + panelName + ".host");
                    String username = config.getString("panels." + panelName + ".username");
                    String password = config.getString("panels." + panelName + ".password");
                    panel = new AMPPanel(panelName, host, username, password);
                    break;
                case "pterodactyl":
                    break;
                default:
                    useLogger("Panel " + panelName + " has an invalid type!");
                    break;
            }

            // Check if panel is online
            if (panel != null && panel.isOnline()) {
                setPanel(panelName, panel);
                useLogger("Panel " + panelName + " is online!");
            } else {
                useLogger("Panel " + panelName + " is offline!");
            }
        }
    }

    /**
     * Initialize servers
     */
    private void initServers() {
        HashMap<?, ?> serverConfig = (HashMap<?, ?>) config.getBlock("servers").getStoredValue();
        for (HashMap.Entry<?, ?> entry: serverConfig.entrySet()) {
            // Get server and panel information
            String serverName = (String) entry.getKey();

            String panelName = config.getString("servers." + serverName + ".panel");
            String instanceName;
            String instanceId;

            // Check if server is a standalone AMP instance
            if (panelName.equals("ampstandalone")) {
                instanceName = config.getString("servers." + serverName + ".name");
                instanceId = config.getString("servers." + serverName + ".id");
                String host = config.getString("servers." + serverName + ".host");
                String username = config.getString("servers." + serverName + ".username");
                String password = config.getString("servers." + serverName + ".password");
                boolean isADS = config.getBoolean("servers." + serverName + ".isADS") != null
                        && config.getBoolean("servers." + serverName + ".isADS");

                AMPPanel panel = new AMPPanel(serverName, host, username, password);
                AMPAPIHandler instanceAPI;
                if (isADS) {
                    instanceAPI = panel.getInstanceAPI(serverName, instanceName, instanceId);
                } else {
                    instanceAPI = new AMPAPIHandler(host, username, password, "", "");
                }
                Server server = new AMPServer(serverName, "ampstandalone", instanceName, instanceId, instanceAPI);

                // Check if server is online
                if (server.isOnline()) {
                    setServer(serverName, server);
                    useLogger("Server " + serverName + " is online!");
                } else {
                    useLogger("Server " + serverName + " is offline!");
                }
                return;
            }

            Panel panel = getPanel(panelName);
            if (panel == null) {
                useLogger("Server " + serverName + "'s panel is offline or defined incorrectly!");
                return;
            }
            String panelType = config.getString("panels." + panelName + ".type");

            Server server = null;
            switch (panelType) {
                case "cubecodersamp":
                    instanceName = config.getString("servers." + serverName + ".name");
                    instanceId = config.getString("servers." + serverName + ".id");

                    AMPAPIHandler instanceAPI = ((AMPPanel) panel).getInstanceAPI(serverName, instanceName, instanceId);
                    server = new AMPServer(serverName, panelName, instanceName, instanceId, instanceAPI);
                    break;
            }

            // Check if server is online
            if (server != null && server.isOnline()) {
                setServer(serverName, server);
                useLogger("Server " + serverName + " is online!");
            } else {
                useLogger("Server " + serverName + " is offline!");
            }
        }
    }

    /**
     * Initialize groups
     */
    private void initGroups() {
        HashMap<?, ?> groupConfig = (HashMap<?, ?>) config.getBlock("groups").getStoredValue();
        for (HashMap.Entry<?, ?> entry: groupConfig.entrySet()) {
            // Get group name
            String groupName = (String) entry.getKey();

            // Check if servers exist
            ArrayList<String> servers = (ArrayList<String>) config.getBlock("groups." + groupName + ".servers").getStoredValue();
            ArrayList<String> groupServers = new ArrayList<>();
            for (String serverName: servers) {
                if (!serverExists(serverName)) {
                    useLogger("Server " + serverName + " does not exist!");
                    continue;
                }
                groupServers.add(serverName);
            }

            // Initialize tasks
            Group group = new Group(groupName, groupServers, new HashMap<>());
            HashMap<String, Object> tasksConfig = (HashMap<String, Object>) config.getBlock("groups." + groupName + ".tasks").getStoredValue();

            for (HashMap.Entry<String, Object> taskConfig: tasksConfig.entrySet()) {
                String taskName = taskConfig.getKey();
                String taskCommand = (String) config.getBlock("groups." + groupName + ".tasks." + taskName + ".command").getStoredValue();
                long taskInterval = (long) (int) config.getBlock("groups." + groupName + ".tasks." + taskName + ".interval").getStoredValue();

                if (config.get("groups." + groupName + ".tasks." + taskName + ".conditions") != null) {
                    // Initialize task conditions
                    HashMap<String, Condition> taskConditions = new HashMap<>();

                    HashMap<String, Object> conditionsConfig = (HashMap<String, Object>) config.getBlock("groups." + groupName + ".tasks." + taskName + ".conditions").getStoredValue();

                    // loop entries
                    for (int i = 1; i <= conditionsConfig.size(); i++) {
                        String conditionNumber = String.valueOf(i);
                        String conditionPlaceholder = config.getString("groups." + groupName + ".tasks." + taskName + ".conditions." + conditionNumber + ".placeholder");
                        String conditionOperator = config.getString("groups." + groupName + ".tasks." + taskName + ".conditions." + conditionNumber + ".operator");
                        String conditionValue = config.getString("groups." + groupName + ".tasks." + taskName + ".conditions." + conditionNumber + ".value");

                        // Build Condition and add to ArrayList
                        Condition condition = new Condition(conditionPlaceholder, conditionOperator, conditionValue);
                        taskConditions.put(conditionNumber, condition);
                    }

                    // Build Task and add to HashMap
                    Task task = new Task(taskName, taskCommand, taskInterval, taskConditions);
                    group.setTask(taskName, task);
                    group.startTask(taskName);
                    useLogger("Group " + groupName + ": Task " + taskName + " initialized!");
                } else {
                    Task task = new Task(taskName, taskCommand, taskInterval, new HashMap<>());
                    group.setTask(taskName, task);
                    useLogger("Group " + groupName + ": Task " + taskName + " has no conditions!");
                }
            }

            // Add group to HashMap
            setGroup(groupName, group);
            useLogger("Group " + groupName + " initialized!");
        }
    }

    /**
     * Getter for the panel HashMap.
     * @param panelName The name of the panel
     * @return The panel instance
     */
    public Panel getPanel(String panelName) {
        return panels.get(panelName);
    }

    /**
     * Setter for the panel HashMap.
     * @param panelName The name of the panel
     * @param panel The panel instance
     */
    public void setPanel(String panelName, Panel panel) {
        panels.put(panelName, panel);
    }

    /**
     * Remove a panel from the HashMap.
     * @param panelName The name of the panel
     */
    public void removePanel(String panelName) {
        panels.remove(panelName);
    }

    /**
     * Check if a panel exists.
     * @param panelName The name of the panel
     * @return Whether the panel exists or not
     */
    public boolean panelExists(String panelName) {
        return panels.containsKey(panelName);
    }

    /**
     * Get Panels
     * @return The names of all panels
     */
    public ArrayList<String> getPanels() {
        return new ArrayList<>(panels.keySet());
    }

    /**
     * Getter for the group HashMap.
     */
    public Group getGroup(String groupName) {
        return groups.get(groupName);
    }

    /**
     * Setter for the group HashMap.
     * @param groupName The name of the group
     * @param group The group instance
     */
    public void setGroup(String groupName, Group group) {
        groups.put(groupName, group);
    }

    /**
     * Remove a group from the HashMap.
     * @param groupName The name of the group
     */
    public void removeGroup(String groupName) {
        groups.remove(groupName);
    }

    /**
     * Check if a group exists.
     * @param groupName The name of the group
     * @return Whether the group exists or not
     */
    public boolean groupExists(String groupName) {
        return groups.containsKey(groupName);
    }

    /**
     * Get Groups
     * @return The names of all groups
     */
    public ArrayList<String> getGroups() {
        return new ArrayList<>(groups.keySet());
    }

    /**
     * Getter for the server HashMap.
     * @param serverName The name of the server
     * @return The server instance
     */
    public Server getServer(String serverName) {
        return servers.get(serverName);
    }

    /**
     * Setter for the server HashMap.
     * @param serverName The name of the server
     * @param server The server instance
     */
    public void setServer(String serverName, Server server) {
        servers.put(serverName, server);
    }

    /**
     * Remove a server from the HashMap.
     * @param serverName The name of the server
     */
    public void removeServer(String serverName) {
        servers.remove(serverName);
    }

    /**
     * Check if a server exists.
     * @param serverName The name of the server
     * @return Whether the server exists or not
     */
    public boolean serverExists(String serverName) {
        return servers.containsKey(serverName);
    }

    /**
     * Get Groups
     * @return The names of all groups
     */
    public ArrayList<String> getServers() {
        return new ArrayList<>(servers.keySet());
    }

    /**
     * Save panel to config.
     * @param panelName The panel to save
     */
    public void savePanelConfig(String panelName) {
        Panel panel = getPanel(panelName);

        // Save Host
        String host = panel.getHost();
        config.set("panels." + panelName + ".host", host);

        // Save AMP Panel
        if (panel instanceof AMPPanel) {
            // Save username
            String username = ((AMPPanel) panel).getUsername();
            config.set("panels." + panelName + ".username", username);

            // Save password
            String password = ((AMPPanel) panel).getPassword();
            config.set("panels." + panelName + ".password", password);
        }

        // Save Pterodactyl Panel TODO: Add Pterodactyl support

        // Save config
        try {
            config.save();
        } catch (Exception e) {
            useLogger("Failed to save config!\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Delete panel from config.
     * @param panelName The panel to delete
     */
    public void deletePanelConfig(String panelName) {
        config.remove("panels." + panelName);
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add/update server id in config.
     * @param serverName The name of the server
     * @param id The InstanceID of the server
     */
    public void addServerID(String serverName, String id) {
        config.set("servers." + serverName + ".id", id);
        try {
            config.save();
        } catch (Exception e) {
            useLogger("Failed to save config!\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save server to config.
     * @param serverName The server to save
     */
    public void saveServerConfig(String serverName) {
        Server server = getServer(serverName);

        // Save Panel Name
        String panelName = server.getPanelName();
        config.set("servers." + serverName + ".panel", panelName);

        // Save AMP server
        if (server instanceof AMPServer) {
            // Save instanceName
            String instanceName = ((AMPServer) server).getInstanceName();
            if (instanceName != null) {
                config.set("servers." + serverName + ".name", instanceName);
            }

            // Save instanceId
            String instanceId = ((AMPServer) server).getInstanceId();
            if (instanceId != null) {
                config.set("servers." + serverName + ".id", instanceId);
            }
        }
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete server from config.
     * @param serverName The server to delete
     */
    public void deleteServerConfig(String serverName) {
        config.remove("servers." + serverName);
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save group servers to config.
     * @param groupName The group for which to save the servers
     */
    public void saveGroupServers(String groupName) {
        config.set("groups." + groupName + ".servers", getGroup(groupName).getServers());
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save group tasks to config.
     * @param groupName The group for which to save the tasks
     */
    public void saveGroupTasks(String groupName) {
        Group group = getGroup(groupName);
        ArrayList<String> tasks = group.getTasks();
        for (String taskName : tasks) {
            config.set("groups." + groupName + ".tasks." + taskName, taskName);

            Task task = group.getTask(taskName);
            config.set("groups." + groupName + ".tasks." + taskName + ".command", task.getCommand());
            config.set("groups." + groupName + ".tasks." + taskName + ".interval", task.getInterval());

            // Save Conditions
            HashMap<String, Condition> conditions = task.getConditions();
            for (int i = 1; i <= conditions.size(); i++) {
                Condition condition = conditions.get(Integer.toString(i));
                if (condition == null) {
                    continue;
                }
                config.set("groups." + groupName + ".tasks." + taskName + ".conditions." + i + ".placeholder", condition.getPlaceholder());
                config.set("groups." + groupName + ".tasks." + taskName + ".conditions." + i + ".operator", condition.getOperator());
                config.set("groups." + groupName + ".tasks." + taskName + ".conditions." + i + ".value", condition.getValue());
            }
        }
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete task from group in config.
     * @param groupName The group for which to delete the task
     * @param taskName The task to delete
     */
    public void deleteTaskConfig(String groupName, String taskName) {
        config.remove("groups." + groupName + ".tasks." + taskName);
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
