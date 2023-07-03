package dev.neuralnexus.serverpanelmanager.common;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.neuralnexus.serverpanelmanager.common.api.ServerPanelManagerAPIProvider;
import dev.neuralnexus.serverpanelmanager.common.cubecodersamp.AMPPanel;
import dev.neuralnexus.serverpanelmanager.common.cubecodersamp.AMPServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static dev.neuralnexus.serverpanelmanager.common.Utils.repeatTaskAsync;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;
import static java.lang.Thread.sleep;

public class ServerPanelManager {
    /**
     * Properties of the ServerPanelManager class.
     * instance: The singleton instance of the ServerPanelManager class
     * config: The config file
     * logger: The logger
     * configPath: The path to the config file
     * STARTED: Whether the PanelServerManager has been started
     * hooks: The hooks
     * panels: The panels
     * servers: The servers
     * groups: The groups
     * commandHandler: The command handler
     */
    private static final ServerPanelManager instance = new ServerPanelManager();
    private static YamlDocument config;
    private static Object logger;
    private static String configPath;
    private static boolean STARTED = false;
    private static final ArrayList<Object> hooks = new ArrayList<>();
    private static final HashMap<String, Panel> panels = new HashMap<>();
    private static final HashMap<String, Server> servers = new HashMap<>();
    private static final HashMap<String, Group> groups = new HashMap<>();

    public static final CommandHandler commandHandler = new CommandHandler(instance);

    /**
     * Constructor for the ServerPanelManager class.
     */
    public ServerPanelManager() {}

    /**
     * Getter for the singleton instance of the ServerPanelManager class.
     * @return The singleton instance
     */
    public static ServerPanelManager getInstance() {
        return instance;
    }

    /**
     * Use whatever logger is being used.
     * @param message The message to log
     */
    public static void useLogger(String message) {
        if (logger instanceof java.util.logging.Logger) {
            ((java.util.logging.Logger) logger).info(message);
        } else if (logger instanceof org.slf4j.Logger) {
            ((org.slf4j.Logger) logger).info(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Add a hook to the hooks list
     * @param hook The hook to add
     */
    public static void addHook(Object hook) {
        hooks.add(hook);
    }

    /**
     * Start ServerPanelManager
     * @param configPath The path to the config file
     * @param logger The logger
     */
    public static void start(String configPath, Object logger) {
        ServerPanelManager.configPath = configPath;
        ServerPanelManager.logger = logger;

        // Config
        try {
            config = YamlDocument.create(new File("." + File.separator + configPath + File.separator + "ServerPanelManager", "config.yml"),
                    Objects.requireNonNull(ServerPanelManager.class.getClassLoader().getResourceAsStream("config.yml"))
            );
            config.reload();
        } catch (IOException | NullPointerException e) {
            useLogger("Failed to load config.yml!\n" + e.getMessage());
            e.printStackTrace();
        }

        if (STARTED) {
            useLogger("[ServerPanelManager] ServerPanelManager has already started!");
            return;
        }
        STARTED = true;

        runTaskAsync(() -> {
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

        useLogger("[ServerPanelManager] ServerPanelManager has been started!");
        ServerPanelManagerAPIProvider.register(instance);
    }

    /**
     * Start TaterAPI
     */
    public static void start() {
        start(configPath, logger);
    }

    /**
     * Stop TaterAPI
     */
    public static void stop() {
        if (!STARTED) {
            useLogger("[ServerPanelManager] ServerPanelManager has already stopped!");
            return;
        }
        STARTED = false;

        useLogger("[ServerPanelManager] ServerPanelManager has been stopped!");
        ServerPanelManagerAPIProvider.unregister();
    }

    /**
     * Reload ServerPanelManager
     */
    public static void reload() {
        if (!STARTED) {
            useLogger("[ServerPanelManager] ServerPanelManager has not been started!");
            return;
        }

        // Remove references to config, panels, servers, and groups
        config = null;
        panels.clear();
        servers.clear();
        groups.clear();

        // Stop ServerPanelManager
        stop();

        // Start ServerPanelManager
        start(configPath, logger);

        useLogger("[ServerPanelManager] ServerPanelManager has been reloaded!");
    }

    /**
     * Initialize panels
     */
    private static void initPanels() {
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
    private static void initServers() {
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
     * Initialize WatchFerret
     */
    private void initWatchFerret(String groupName) {
        // Start WatchFerret
        repeatTaskAsync(() -> {
            useLogger("Running WatchFerret for group " + groupName + "...");
            // Get group
            Group group = getGroup(groupName);
            if (group == null) {
                useLogger("Group " + groupName + " does not exist!");
                return;
            }

            // Get servers
            ArrayList<String> servers = group.getServers();
            if (servers.size() == 0) {
                useLogger("Group " + groupName + " has no servers!");
                return;
            }

            // Loop through servers
            for (String serverName: servers) {
                Server server = getServer(serverName);
                if (!(server instanceof AMPServer)) {
                    useLogger("Server " + serverName + " does not exist!");
                    continue;
                }

                // Get server status
                Map<String, Object> status = server.getStatus();
                if (status == null) {
                    useLogger("Server " + serverName + " is offline!");
                    continue;
                }

                if (status.containsKey("State")) {
                    String state = (String) status.get("State");
                    if (state.equals("Restarting")) {
                        if (group.getVariable("restart") == null) {
                            group.setVariable("restart", 1);
                        } else {
                            int restartPings = (int) group.getVariable("restart");
                            useLogger("Server " + serverName + " is restarting! + " + restartPings + " restart pings");
                            if (restartPings >= 3) {
                                useLogger("Rescuing server " + serverName + "...");
                                server.killServer();
                                try {
                                    sleep(1000L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                server.startServer();
                                group.setVariable("restart", 0);
                                useLogger("Server " + serverName + " has been rescued!");
                            }
                            group.setVariable("restart", restartPings + 1);
                        }
                    } else if (state.equals("Running")) {
                        group.setVariable("restart", 0);
                    }
                }
            }
        }, 0L, 20*300L);
    }

    /**
     * Initialize groups
     */
    private static void initGroups() {
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

            Group group = new Group(groupName, groupServers);

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
    public static Panel getPanel(String panelName) {
        return panels.get(panelName);
    }

    /**
     * Setter for the panel HashMap.
     * @param panelName The name of the panel
     * @param panel The panel instance
     */
    public static void setPanel(String panelName, Panel panel) {
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
    public static void setGroup(String groupName, Group group) {
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
    public static void setServer(String serverName, Server server) {
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
    public static boolean serverExists(String serverName) {
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
}
