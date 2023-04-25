package ca.sperrer.p0t4t0sandwich.ampservermanager;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ca.sperrer.p0t4t0sandwich.ampservermanager.Utils.runTaskAsync;

public class AMPServerManager {
    /*
    Properties of the AMPServerManager class.
    config: The config file
    logger: The logger
    host: The host URL of the AMP instance
    username: The AMP username
    password: The AMP password
    instances: A HashMap of instances
    singleton: The singleton instance of the AMPServerManager class
     */
    private static YamlDocument config;
    private final Object logger;
    private String host;
    private String username;
    private String password;
    private static final HashMap<String, Instance> instances = new HashMap<>();
    private static AMPServerManager singleton;

    /*
    Getter for the singleton instance of the AMPServerManager class.
    @return: The singleton instance
     */
    public static AMPServerManager getInstance() {
        return singleton;
    }

    /*
    Constructor for the AMPServerManager class.
    @param configPath: The path to the config file
    @param logger: The logger
    @return: The singleton instance
     */
    public AMPServerManager(String configPath, Object logger) {
        singleton = this;
        this.logger = logger;

        // Config
        try {
            config = YamlDocument.create(new File("./" + configPath + "/AMPServerManager", "config.yml"),
                    Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("config.yml"))
            );
            config.reload();

            // Get AMP host, username, and password
            host = config.getString("amp.host");
            username = config.getString("amp.username");
            password = config.getString("amp.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Getter for the instance HashMap.
    @param serverName: The name of the server
    @return: The instance
     */
    public static Instance getServerInstance(String serverName) {
        return instances.get(serverName);
    }

    /*
    Setter for the instance HashMap.
    @param serverName: The name of the server
    @param instance: The instance
     */
    private void setServerInstance(String serverName, Instance instance) {
        instances.put(serverName, instance);
    }

    /*
    Remove an instance from the HashMap.
    @param serverName: The name of the server
     */
    private void removeServerInstance(String serverName) {
        instances.remove(serverName);
    }

    /*
    Check if an instance exists.
    @param serverName: The name of the server
    @return: Whether the instance exists or not
     */
    public static boolean serverInstanceExists(String serverName) {
        return instances.containsKey(serverName);
    }

    /*
    Use whatever logger is being used.
    @param logger: The logger
    @param message: The message to log
     */
    public void useLogger(Object logger, String message) {
        if (logger instanceof java.util.logging.Logger) {
            ((java.util.logging.Logger) logger).info(message);
        } else if (logger instanceof org.slf4j.Logger) {
            ((org.slf4j.Logger) logger).info(message);
        } else if (logger instanceof org.apache.logging.log4j.Logger) {
            ((org.apache.logging.log4j.Logger) logger).info(message);
        }
    }

    /*
    Start the AMPServerManager.
     */
    public void start() {
        runTaskAsync(() -> {
            // Get and initialize instances
            Map<String, Object> serverConfig = (Map<String, Object>) config.getBlock("servers").getStoredValue();
            for (Map.Entry<String, Object> entry: serverConfig.entrySet()) {
                // Get instance name and id
                String serverName = entry.getKey();
                String instanceName = config.getString("servers." + serverName + ".name");
                String instanceId = config.getString("servers." + serverName + ".id");

                Instance instance = new Instance(host, username, password, true, serverName, instanceName, instanceId);

                // Check if instance is online
                runTaskAsync(() -> {
                    if (instance.APILogin()) {
                        setServerInstance(serverName, instance);
                        useLogger(logger, "Instance " + instanceName + " is online!");
                    } else {
                        useLogger(logger, "Instance " + instanceName + " is offline!");
                    }
                });
            }

            // Initialize groups
            Map<String, Object> groupConfig = (Map<String, Object>) config.getBlock("groups").getStoredValue();
            for (Map.Entry<String, Object> entry: groupConfig.entrySet()) {
                // Get group name and servers
                String groupName = entry.getKey();
                ArrayList<String> servers = (ArrayList<String>) config.getBlock("groups." + groupName + ".servers").getStoredValue();

                // Loop through tasks
            }
        });
    }

    /*
    Add/update instance id in config.
    @param serverName: The name of the server
    @param id: The InstanceID of the server
     */
    public void addInstanceID(String serverName, String id) {
        config.set("servers." + serverName + ".id", id);
        try {
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    Generic handler for command responses.
    @param args: The command arguments
    @param rootCommand: The root command
    @param usage: The usage of the command
    @param exemptStates: Server states that are exempt from the command
    @param message: The message to send if the command is successful
    @param exemptStateMessage: The message to send if the server is in an exempt state
    @return: The response
     */
    private String genericHandler(String[] args, String rootCommand, String usage, List<Integer> exemptStates, String message, String exemptStateMessage) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: " + rootCommand +" " + usage;
        }

        // Check if there are enough arguments
        if (args.length == 2) {
            String serverName = args[1];
            if (serverInstanceExists(serverName)) {
                Instance instance = getServerInstance(serverName);
                Map<String, Object> status = instance.getStatus();
                int state = (int) Math.round((double) status.get("State"));

                // Check if server is in exempt state
                if (exemptStates.isEmpty() || !exemptStates.contains(state)) {
                    switch (args[0]) {
                        case "start":
                            instance.startServer();
                            break;
                        case "stop":
                            instance.stopServer();
                            break;
                        case "restart":
                            instance.restartServer();
                            break;
                        case "kill":
                            instance.killServer();
                            break;
                        case "sleep":
                            instance.sleepServer();
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

    /*
    Start Server Handler
    @param args: The command arguments
    @return: The response
     */
    private String startServerHandler(String[] args) {
        return genericHandler(args, "/amp", "start <server>", Arrays.asList(10, 20), "§aStarting server...", "§cServer is already running!");
    }

    /*
    Stop Server Handler
    @param args: The command arguments
    @return: The response
     */
    private String stopServerHandler(String[] args) {
        return genericHandler(args, "/amp", "stop <server>", Arrays.asList(0, 40), "§aStopping server...", "§cServer is already stopped!");
    }

    /*
    Restart Server Handler
    @param args: The command arguments
    @return: The response
     */
    private String restartServerHandler(String[] args) {
        return genericHandler(args, "/amp", "restart <server>", Arrays.asList(0, 40), "§aRestarting server...", "§cServer is already stopped!");
    }

    /*
    Kill Server Handler
    @param args: The command arguments
    @return: The response
     */
    private String killServerHandler(String[] args) {
        return genericHandler(args, "/amp", "kill <server>", Collections.emptyList(), "§aKilling server...", "§cServer is already stopped!");
    }

    /*
    Sleep Server Handler
    @param args: The command arguments
    @return: The response
     */
    private String sleepServerHandler(String[] args) {
        return genericHandler(args, "/amp", "sleep <server>", Arrays.asList(0, 30, 40), "§aPutting server to sleep...", "§cServer is already stopped/sleeping!");
    }

    /*
    Send Command Server Handler
    @param args: The command arguments
    @return: The response
     */
    private String sendCommandHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp command <instance> <command>";
        }
        // Send command
        if (args.length >= 3) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Instance instance = instances.get(serverName);
                Map<String, Object> status = instance.getStatus();
                int state = (int) Math.round((double) status.get("State"));
                if (Objects.equals(state, 20)) {
                    String command = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    instance.sendCommand(command);
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

    /*
    Get Status Handler
    @param args: The command arguments
    @return: The response
     */
    private String getStatusHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp status <instance>";
        }
        // Get status
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Instance instance = instances.get(serverName);
                Map<String, Object> status = instance.parseStatus(instance.getStatus());
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

    /*
    Backup Server Handler
    @param args: The command arguments
    @return: The response
     */
    private String backupServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp backup <instance>";
        }
        // Backup server
        if (args.length >= 2) {
            String serverName = args[1];

            if (instances.containsKey(serverName)) {
                Instance instance = instances.get(serverName);
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

                instance.backupServer(backupName, backupDescription, isSticky);
                return "§aBacking up server " + serverName + "...";
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    /*
    Player List Handler
    @param args: The command arguments
    @return: The response
     */
    private String playerListHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp players <instance>";
        }
        // Get player list
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Instance instance = instances.get(serverName);
                Map<String, Object> status = instance.getStatus();
                int state = (int) Math.round((double) status.get("State"));
                if (Objects.equals(state, 20)) {
                    String output = "§6" + serverName + ":\n";

                    // Player count
                    if (status.containsKey("Metrics")) {
                        Map<String, Object> metrics = (Map<String, Object>) status.get("Metrics");
                        if (metrics.containsKey("Active Users")) {
                            Map<String, Object> Players = (Map<String, Object>) metrics.get("Active Users");
                            int PlayersValue = (int) Math.round((double) Players.get("RawValue"));
                            int PlayersMax = (int) Math.round((double) Players.get("MaxValue"));
                            output += "§6Players: §9" + PlayersValue + "§6/§9" + PlayersMax + "\n";
                        }
                    }

                    // Player list
                    List<String> playerList = instance.parsePlayerList(instance.getPlayerList());
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

    /*
    Command Messenger
    @param args: The command arguments
    @return: The response
     */
    public String commandMessenger(String[] args) {
        String message;
        try {
            switch (args[0].toLowerCase()) {
                // Server Command Tree
                case "server":
                    switch (args[1].toLowerCase()) {
                        // List
                        case "list":
                            // Get server list from config "servers" object keys
                            message = "§6Available servers: §5" + String.join("§6, §5", instances.keySet());
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
            Instance instance = instances.get(args[1]);
            boolean result = instance.APILogin();
            if (result) {
                message = "§cAn error occurred while executing the command!";
            } else {
                message = "§cAn error occurred while logging into the instance!";
            }
        }
        return message;
    }
}
