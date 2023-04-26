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
     */
    private static YamlDocument config;
    private final Object logger;
    private static final HashMap<String, Panel> panels = new HashMap<>();
    private static final HashMap<String, Server> servers = new HashMap<>();
    private static PanelServerManager singleton = null;

    /**
     * Getter for the singleton instance of the PanelServerManager class.
     * @return The singleton instance
     */
    public static PanelServerManager getInstance() {
        return singleton;
    }

    /**
     * Constructor for the PanelServerManager class.
     * @param configPath: The path to the config file
     * @param logger: The logger
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
            e.printStackTrace();
        }
    }

    /**
     * Use whatever logger is being used.
     * @param logger: The logger
     * @param message: The message to log
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
        runTaskAsync(() -> {
            // Initialize Panels
            Map<?, ?> panelConfig = (Map<?, ?>) config.getBlock("panels").getStoredValue();
            for (Map.Entry<?, ?> entry: panelConfig.entrySet()) {
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
                }

                // Check if panel is online
                if (panel != null && panel.isOnline()) {
                    panels.put(panelName, panel);
                    useLogger(logger, "Panel " + panelName + " is online!");
                } else {
                    useLogger(logger, "Panel " + panelName + " is offline!");
                }
            }

            // Initialize servers
            Map<?, ?> serverConfig = (Map<?, ?>) config.getBlock("servers").getStoredValue();
            for (Map.Entry<?, ?> entry: serverConfig.entrySet()) {
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
                    continue;
                }

                Panel panel = panels.get(panelName);
                if (panel == null) {
                    useLogger(logger, "Server " + serverName + "'s panel is offline or defined incorrectly!");
                    continue;
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

//            // Initialize groups
//            Map<String, Object> groupConfig = (Map<String, Object>) config.getBlock("groups").getStoredValue();
//            for (Map.Entry<String, Object> entry: groupConfig.entrySet()) {
//                // Get group name and servers
//                String groupName = entry.getKey();
//                ArrayList<String> servers = (ArrayList<String>) config.getBlock("groups." + groupName + ".servers").getStoredValue();
//
//                useLogger(logger, "Group " + servers.toString());
//
//                // Loop through tasks
//            }
        });
    }

    /**
     * Getter for the server HashMap.
     * @param serverName: The name of the server
     * @return The instance
     */
    public static Server getServer(String serverName) {
        return servers.get(serverName);
    }

    /**
     * Setter for the server HashMap.
     * @param serverName: The name of the server
     * @param server: The server instance
     */
    private void setServer(String serverName, Server server) {
        servers.put(serverName, server);
    }

    /**
     * Remove a server from the HashMap.
     * @param serverName: The name of the server
     */
    private void removeServer(String serverName) {
        servers.remove(serverName);
    }

    /**
     * Check if a server exists.
     * @param serverName: The name of the server
     * @return Whether the server exists or not
     */
    public static boolean serverInstanceExists(String serverName) {
        return servers.containsKey(serverName);
    }

    /**
     * Add/update server id in config.
     * @param serverName: The name of the server
     * @param id: The InstanceID of the server
     */
    public void addServerID(String serverName, String id) {
        config.set("servers." + serverName + ".id", id);
        try {
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generic handler for command responses.
     * @param args: The command arguments
     * @param rootCommand: The root command
     * @param usage: The usage of the command
     * @param exemptStates: Server states that are exempt from the command
     * @param message: The message to send if the command is successful
     * @param exemptStateMessage: The message to send if the server is in an exempt state
     * @return The response
     */
    private String genericHandler(String[] args, String rootCommand, String usage, List<String> exemptStates, String message, String exemptStateMessage) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: " + rootCommand +" " + usage;
        }

        // Check if there are enough arguments
        if (args.length == 2) {
            String serverName = args[1];
            if (serverInstanceExists(serverName)) {
                Server server = getServer(serverName);
                Map<String, Object> status = server.getStatus();
                String state = (String) status.get("State");

                // Check if server is in exempt state
                if (exemptStates.isEmpty() || !exemptStates.contains(state)) {
                    switch (args[0]) {
                        case "start":
                            server.startServer();
                            break;
                        case "stop":
                            server.stopServer();
                            break;
                        case "restart":
                            server.restartServer();
                            break;
                        case "kill":
                            server.killServer();
                            break;
                        case "sleep":
                            if (server instanceof AMPServer) {
                                ((AMPServer) server).sleepServer();
                            }
                            break;
                    }
                    return message;
                } else {
                    return exemptStateMessage;
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    /**
     * Start Server Handler
     * @param args: The command arguments
     * @return The response
     */
    private String startServerHandler(String[] args) {
        return genericHandler(args, "/psm", "start <server>", Arrays.asList("Starting", "Ready"), "§aStarting server...", "§cServer is already running!");
    }

    /**
     * Stop Server Handler
     * @param args: The command arguments
     * @return The response
     */
    private String stopServerHandler(String[] args) {
        return genericHandler(args, "/psm", "stop <server>", Arrays.asList("Stopped", "Stopping"), "§aStopping server...", "§cServer is already stopped!");
    }

    /**
     * Restart Server Handler
     * @param args: The command arguments
     * @return The response
     */
    private String restartServerHandler(String[] args) {
        return genericHandler(args, "/psm", "restart <server>", Arrays.asList("Stopped", "Stopping"), "§aRestarting server...", "§cServer is already stopped!");
    }

    /**
     * Kill Server Handler
     * @param args: The command arguments
     * @return The response
     */
    private String killServerHandler(String[] args) {
        return genericHandler(args, "/psm", "kill <server>", Collections.emptyList(), "§aKilling server...", "§cServer is already stopped!");
    }


    /**
     * Sleep Server Handler
     * @param args: The command arguments
     * @return The response
     */
    private String sleepServerHandler(String[] args) {
        return genericHandler(args, "/psm", "sleep <server>", Arrays.asList("Stopped", "Restarting", "Stopping"), "§aPutting server to sleep...", "§cServer is already stopped/sleeping!");
    }

    /**
     * Send Command Server Handler
     * @param args: The command arguments
     * @return The response
     */
    private String sendCommandHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /psm command <instance> <command>";
        }
        // Send command
        if (args.length >= 3) {
            String serverName = args[1];
            if (servers.containsKey(serverName)) {
                Server server = servers.get(serverName);
                Map<String, Object> status = server.getStatus();
                String state = (String) status.get("State");
                if (Objects.equals(state, "Ready")) {
                    String command = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    server.sendCommand(command);
                    return "§aSending command to server " + serverName + "...";
                } else {
                    return "§cServer " + serverName + " is not Ready!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    /**
     * Get Status Handler
     * @param args: The command arguments
     * @return The response
     */
    private String getStatusHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /psm status <instance>";
        }
        // Get status
        if (args.length == 2) {
            String serverName = args[1];
            if (servers.containsKey(serverName)) {
                Server server = servers.get(serverName);
                Map<String, Object> status = server.getStatus();
                if (status == null) {
                    return "§cServer " + serverName + " is not responding!";
                }

                String output = "§6" + serverName + ":";

                if (status.containsKey("State")) {
                    output += "\n§6Status: §5" + status.get("State");
                }

                if (status.containsKey("CPU")) {
                    output += "\n§6CPU: §9" + status.get("CPU") + "§6%";
                }

                if (status.containsKey("Memory") && status.containsKey("MemoryMax")) {
                    output += "\n§6Memory: §9" + status.get("MemoryValue") + "§6/§9" + status.get("MemoryMax") + "§6MB";
                }

                if (status.containsKey("PlayersMax")) {
                    output += "\n§6Players: §9" + status.get("PlayersValue") + "§6/§9" + status.get("PlayersMax");
                }

                if (status.containsKey("TPSValue")) {
                    output += "\n§6TPS: §2" + status.get("TPSValue");
                }

                return output;
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    /**
     * Get Console Handler
     * @param args: The command arguments
     * @return The response
     */
    private String backupServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /psm backup <instance>";
        }
        // Backup server
        if (args.length >= 2) {
            String serverName = args[1];

            if (servers.containsKey(serverName)) {
                Server server = servers.get(serverName);
                // Parse Backup details
                String backupName = args.length >= 3 ? args[2] : "";
                String backupDescription = args.length >= 4 ? args[3] : "";
                boolean isSticky = args.length >= 5 && (
                        args[4].equalsIgnoreCase("true")
                        || args[4].equalsIgnoreCase("t")
                        || args[4].equalsIgnoreCase("1")
                        || args[4].equalsIgnoreCase("yes")
                        || args[4].equalsIgnoreCase("y")
                );
                if (!(server instanceof AMPServer)) {
                    return "§cServer " + serverName + " is not an AMP server!";
                }
                ((AMPServer) server).backupServer(backupName, backupDescription, isSticky);
                return "§aBacking up server " + serverName + "...";
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    /**
     * Player List Handler
     * @param args: The command arguments
     * @return The response
     */
    private String playerListHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /psm players <instance>";
        }
        // Get player list
        if (args.length == 2) {
            String serverName = args[1];
            if (servers.containsKey(serverName)) {
                Server server = servers.get(serverName);
                Map<String, Object> status = server.getStatus();
                String state = (String) status.get("State");
                if (Objects.equals(state, "Ready")) {
                    String output = "§6" + serverName + ":\n";

                    // Player count
                    if (status.containsKey("Metrics")) {
                        Map<?, ?> metrics = (Map<?, ?>) status.get("Metrics");
                        if (metrics.containsKey("Active Users")) {
                            Map<?, ?> Players = (Map<?, ?>) metrics.get("Active Users");
                            int PlayersValue = (int) Math.round((double) Players.get("RawValue"));
                            int PlayersMax = (int) Math.round((double) Players.get("MaxValue"));
                            output += "§6Players: §9" + PlayersValue + "§6/§9" + PlayersMax + "\n";
                        }
                    }

                    // Player list
                    List<String> playerList = server.getPlayerList();
                    if (playerList == null || playerList.isEmpty()) {
                        return "§cServer " + serverName + " has no players online!";
                    }
                    return output + String.join("\n", playerList);
                } else {
                    return "§cServer " + serverName + " is not Ready!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    private String helpHandler() {
        return "§6Available commands:" +
            "\n§6help - Show this message" +
            "\n§6exit - Exit the application (CLI only)" +
            "\n§6start <server> - Start server" +
            "\n§6stop <server> - Stop server" +
            "\n§6restart <server> - Restart server" +
            "\n§6kill <server> - Kill server" +
            "\n§6sleep <server> - Put server to sleep" +
            "\n§6send <server> <command> - Send command to server" +
            "\n§6status <server> - Get server status" +
            "\n§6backup <server> [name] [description] [sticky <- true or false] - Backup server" +
            "\n§6players <server> - Get server player list" +
            "\n§6server list - List available servers" +
            "\n§6server add <server> <instanceName> [instanceID] - Add server to config" +
            "\n§6server remove <server> - Remove server from config";
    }

    private String serverCommand(String[] args) {
        String message;
        switch (args[1].toLowerCase()) {
            // List
            case "list":
                // Get server list from config "servers" object keys
                message = "§6Available servers: §5\n" + String.join("\n", servers.keySet());
                break;
            // Add
            case "add":
                // Add server to config "servers" object
                message = "";
                break;
            // Remove
            case "remove":
                // Remove server from config "servers" object
                message = "";
                break;
            default:
                // TODO: Need specific help handler
                message = helpHandler();
                break;
        }
        return message;
    }

    /**
     * Command Messenger
     * @param args The command arguments
     * @return The response
     */
    public String commandMessenger(String[] args) {
        String message;
        try {
            switch (args[0].toLowerCase()) {
                // Server Command Tree
                case "server":
                    message = serverCommand(args);
                    break;

                // Start Server
                case "start":
                    message = startServerHandler(args);
                    break;
                // Stop Server
                case "stop":
                    message = stopServerHandler(args);
                    break;
                // Restart Server
                case "restart":
                    message = restartServerHandler(args);
                    break;
                // Kill Server
                case "kill":
                    message = killServerHandler(args);
                    break;
                // Sleep Server
                case "sleep":
                    message = sleepServerHandler(args);
                    break;
                // Send Command
                case "send":
                    message = sendCommandHandler(args);
                    break;
                // Get Status
                case "status":
                    message = getStatusHandler(args);
                    break;
                // Backup Server
                case "backup":
                    message = backupServerHandler(args);
                    break;
                // Player List
                case "players":
                    message = playerListHandler(args);
                    break;
                // Help
                default:
                    message = helpHandler();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Server server = servers.get(args[1]);
            boolean result = server.reLog();
            if (result) {
                message = "§cAn error occurred while executing the command!";
            } else {
                message = "§cAn error occurred while logging into the instance!";
            }
        }
        return message;
    }
}