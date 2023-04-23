package ca.sperrer.p0t4t0sandwich.ampservermanager;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ca.sperrer.p0t4t0sandwich.ampservermanager.Utils.runTaskAsync;

public class AMPServerManager {
    public static YamlDocument config;
    public Object logger;
    public String host;
    public String username;
    public String password;
    public static AMPAPIHandler ADS;
    public static HashMap<String, Instance> instances = new HashMap<>();
    private static AMPServerManager singleton;
    public static AMPServerManager getInstance() {
        return singleton;
    }

    // Constructor
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

    // Variable Logger handler
    public void useLogger(Object logger, String message) {
        if (logger instanceof java.util.logging.Logger) {
            ((java.util.logging.Logger) logger).info(message);
        } else if (logger instanceof org.slf4j.Logger) {
            ((org.slf4j.Logger) logger).info(message);
        } else if (logger instanceof org.apache.logging.log4j.Logger) {
            ((org.apache.logging.log4j.Logger) logger).info(message);
        }
    }

    // Start AMPAPIHandler
    public void start() {
        runTaskAsync(() -> {
            ADS = new AMPAPIHandler(host, username, password, "", "");
            ADS.Login();

            // Get and initialize instances
            Map<String, Object> serverConfig = (Map<String, Object>) config.getBlock("servers").getStoredValue();
            for (Map.Entry<String, Object> entry: serverConfig.entrySet()) {
                // Get instance name and id
                String serverName = entry.getKey();
                String name = config.getString("servers." + serverName + ".name");
                String id = config.getString("servers." + serverName + ".id");

                Instance instance = new Instance(host, username, password, true, name, id, null);

                // Check if instance is online
                runTaskAsync(() -> {
                    if (instance.APILogin()) {
                        instances.put(instance.name, instance);
                        useLogger(logger, "Instance " + instance.name + " is online!");
                    } else {
                        useLogger(logger, "Instance " + instance.name + " is offline!");
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

    // Start Server Handler
    private String startServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp start <server>";
        }
        // Start server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Instance instance = instances.get(serverName);
                Map<String, Object> status = instance.getStatus();
                int state = (int) Math.round((double) status.get("State"));
                if (!Objects.equals(state, 10) && !Objects.equals(state, 20)) {
                    instance.startServer();
                    return "§aStarting server " + serverName + "...";
                } else {
                    return "§cServer " + serverName + " is already running!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Stop Server Handler
    private String stopServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp stop <instance>";
        }
        // Stop server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Instance instance = instances.get(serverName);
                Map<String, Object> status = instance.getStatus();
                int state = (int) Math.round((double) status.get("State"));
                if (!Objects.equals(state, 0) && !Objects.equals(state, 40)) {
                    instance.stopServer();
                    return "§aStopping server " + serverName + "...";
                } else {
                    return "§cServer " + serverName + " is already stopped!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Restart Server Handler
    private String restartServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp restart <instance>";
        }
        // Restart server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Instance instance = instances.get(serverName);
                Map<String, Object> status = instance.getStatus();
                int state = (int) Math.round((double) status.get("State"));
                if (!Objects.equals(state, 0) && !Objects.equals(state, 40) && !Objects.equals(state, 45) && !Objects.equals(state, 50) ) {
                    instance.restartServer();
                    return "§aRestarting server " + serverName + "...";
                } else {
                    return "§cServer " + serverName + " is stopped/sleeping!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Kill Server Handler
    private String killServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp kill <instance>";
        }
        // Kill server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Instance instance = instances.get(serverName);
                instance.killServer();
                return "§aKilling server " + serverName + "...";
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Sleep Server Handler
    private String sleepServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp sleep <instance>";
        }
        // Sleep server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Instance instance = instances.get(serverName);
                Map<String, Object> status = instance.getStatus();
                int state = (int) Math.round((double) status.get("State"));
                if (!Objects.equals(state, 0) && !Objects.equals(state, 30) && !Objects.equals(state, 40)) {
                    instance.sleepServer();
                    return "§aPutting server " + serverName + " to sleep...";
                } else {
                    return "§cServer " + serverName + " is already stopped/sleeping!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Send Command Handler
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

    // Get Status Handler
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

    // Backup Server Handler
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
                boolean isSticky = args.length >= 5 && args[4].equalsIgnoreCase("true");

                instance.backupServer(backupName, backupDescription, isSticky);
                return "§aBacking up server " + serverName + "...";
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
            "\n§6server list - List available servers" +
            "\n§6server add <server> <instanceName> [instanceID] - Add server to config" +
            "\n§6server remove <server> - Remove server from config";
    }

    // Command Messenger
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
