package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import ca.sperrer.p0t4t0sandwich.panelservermanager.api.PanelServerManagerProvider;
import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.cubecodersamp.AMPPanel;
import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.cubecodersamp.AMPServer;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ca.sperrer.p0t4t0sandwich.panelservermanager.Utils.runTaskAsync;

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
    public final CommandHandler commandHandler = new CommandHandler();
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
            useLogger(logger, "Failed to load config.yml!\n" + e.getMessage());
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
     * @param logger The logger
     * @param message The message to log
     */
    private void useLogger(Object logger, String message) {
        if (logger instanceof java.util.logging.Logger) {
            ((java.util.logging.Logger) logger).info(message);
        } else if (logger instanceof org.slf4j.Logger) {
            ((org.slf4j.Logger) logger).info(message);
        } else if (logger instanceof org.apache.logging.log4j.Logger) {
            ((org.apache.logging.log4j.Logger) logger).info(message);
        }
    }

    /**
     * Start the PanelServerManager.
     */
    public void start() {
        if (STARTED) {
            useLogger(logger, "PanelServerManager is already started!");
            return;
        }
        runTaskAsync(() -> {
            STARTED = true;
            // Initialize Panels
            useLogger(logger, "Initializing panels...");
            initPanels();

            // Initialize servers
            useLogger(logger, "Initializing servers...");
            initServers();

            // Initialize groups
            useLogger(logger, "Initializing groups...");
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
                    useLogger(logger, "Panel " + panelName + " has an invalid type!");
                    break;
            }

            // Check if panel is online
            if (panel != null && panel.isOnline()) {
                setPanel(panelName, panel);
                useLogger(logger, "Panel " + panelName + " is online!");
            } else {
                useLogger(logger, "Panel " + panelName + " is offline!");
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
                Server server = new AMPServer(serverName, instanceName, instanceId, instanceAPI);

                // Check if server is online
                if (server.isOnline()) {
                    setServer(serverName, server);
                    useLogger(logger, "Server " + serverName + " is online!");
                } else {
                    useLogger(logger, "Server " + serverName + " is offline!");
                }
                return;
            }

            Panel panel = getPanel(panelName);
            if (panel == null) {
                useLogger(logger, "Server " + serverName + "'s panel is offline or defined incorrectly!");
                return;
            }
            String panelType = config.getString("panels." + panelName + ".type");

            Server server = null;
            switch (panelType) {
                case "cubecodersamp":
                    instanceName = config.getString("servers." + serverName + ".name");
                    instanceId = config.getString("servers." + serverName + ".id");

                    AMPAPIHandler instanceAPI = ((AMPPanel) panel).getInstanceAPI(serverName, instanceName, instanceId);
                    server = new AMPServer(serverName, instanceName, instanceId, instanceAPI);
                    break;
            }

            // Check if server is online
            if (server != null && server.isOnline()) {
                setServer(serverName, server);
                useLogger(logger, "Server " + serverName + " is online!");
            } else {
                useLogger(logger, "Server " + serverName + " is offline!");
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
                    useLogger(logger, "Server " + serverName + " does not exist!");
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

                // Initialize task conditions
                ArrayList<Condition> taskConditions = new ArrayList<>();
                ArrayList<HashMap<?, ?>> conditionsConfig = (ArrayList<HashMap<?, ?>>) config.get("groups." + groupName + ".tasks." + taskName + ".conditions");
                for (HashMap<?, ?> conditionConfig: conditionsConfig) {
                    String conditionPlaceholder = (String) conditionConfig.get("placeholder");
                    String conditionOperator = (String) conditionConfig.get("operator");
                    int conditionValue = (int) conditionConfig.get("value");

                    // Build Condition and add to ArrayList
                    Condition condition = new Condition(conditionPlaceholder, conditionOperator, conditionValue);
                    taskConditions.add(condition);
                }

                // Build Task and add to HashMap
                Task task = new Task(taskName, taskCommand, taskInterval, groupServers, taskConditions);
                group.setTask(taskName, task);
                group.startTask(taskName);
            }

            // Add group to HashMap
            setGroup(groupName, group);
            useLogger(logger, "Group " + groupName + " initialized!");
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
    private void setPanel(String panelName, Panel panel) {
        panels.put(panelName, panel);
    }

    /**
     * Remove a panel from the HashMap.
     * @param panelName The name of the panel
     */
    private void removePanel(String panelName) {
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
    private void setGroup(String groupName, Group group) {
        groups.put(groupName, group);
    }

    /**
     * Remove a group from the HashMap.
     * @param groupName The name of the group
     */
    private void removeGroup(String groupName) {
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
    private void setServer(String serverName, Server server) {
        servers.put(serverName, server);
    }

    /**
     * Remove a server from the HashMap.
     * @param serverName The name of the server
     */
    private void removeServer(String serverName) {
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
     * Add/update server id in config.
     * @param serverName The name of the server
     * @param id The InstanceID of the server
     */
    public void addServerID(String serverName, String id) {
        config.set("servers." + serverName + ".id", id);
        try {
            config.save();
        } catch (Exception e) {
            useLogger(logger, "Failed to save config!\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save group to config.
     * @param groupName The group to save
     */
    public void saveGroupServers(String groupName) {
        config.set("groups." + groupName + ".servers", getGroup(groupName).getServers());
        try {
            config.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
