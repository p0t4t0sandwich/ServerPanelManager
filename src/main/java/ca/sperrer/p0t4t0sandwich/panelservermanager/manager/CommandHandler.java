package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.cubecodersamp.AMPPanel;
import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.cubecodersamp.AMPServer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CommandHandler {
    private final PanelServerManager psm;
    /**
     * Constructor for the CommandHandler class
     * @param psm The PanelServerManager instance
     */
    public CommandHandler(PanelServerManager psm) {
        this.psm = psm;
    }

    /**
     * Generic handler for server commands.
     * @param args The command arguments
     * @param rootCommand The root command
     * @param usage The usage of the command
     * @param exemptStates Server states that are exempt from the command
     * @param message The message to send if the command is successful
     * @param exemptStateMessage The message to send if the server is in an exempt state
     * @return The response
     */
    private String genericHandler(String[] args, String rootCommand, String usage, List<String> exemptStates, String message, String exemptStateMessage) {
        try {
            // Usage
            if (args.length == 1) {
                return "§cUsage: " + rootCommand +" " + usage;
            }

            // Check if there are enough arguments
            if (args.length == 2) {
                String serverName = args[1];
                if (psm.serverExists(serverName)) {
                    Server server = psm.getServer(serverName);
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
        } catch (Exception e) {
            System.out.println("An error occurred!\n" + e.getMessage());
            return "§cAn error occurred! Check console for more info.";
        }
    }

    /**
     * Start Server Handler
     * @param args The command arguments
     * @return The response
     */
    private String startServerHandler(String[] args) {
        return genericHandler(args, "/psm", "start <server>", Arrays.asList("Starting", "Ready"), "§aStarting server...", "§cServer is already running!");
    }

    /**
     * Stop Server Handler
     * @param args The command arguments
     * @return The response
     */
    private String stopServerHandler(String[] args) {
        return genericHandler(args, "/psm", "stop <server>", Arrays.asList("Stopped", "Stopping"), "§aStopping server...", "§cServer is already stopped!");
    }

    /**
     * Restart Server Handler
     * @param args The command arguments
     * @return The response
     */
    private String restartServerHandler(String[] args) {
        return genericHandler(args, "/psm", "restart <server>", Arrays.asList("Stopped", "Stopping"), "§aRestarting server...", "§cServer is already stopped!");
    }

    /**
     * Kill Server Handler
     * @param args The command arguments
     * @return The response
     */
    private String killServerHandler(String[] args) {
        return genericHandler(args, "/psm", "kill <server>", Collections.emptyList(), "§aKilling server...", "§cServer is already stopped!");
    }

    /**
     * Sleep Server Handler
     * @param args The command arguments
     * @return The response
     */
    private String sleepServerHandler(String[] args) {
        return genericHandler(args, "/psm", "sleep <server>", Arrays.asList("Stopped", "Restarting", "Stopping"), "§aPutting server to sleep...", "§cServer is already stopped/sleeping!");
    }

    /**
     * Send Command Server Handler
     * @param args The command arguments
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
            if (psm.serverExists(serverName)) {
                Server server = psm.getServer(serverName);
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
     * @param args The command arguments
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
            if (psm.serverExists(serverName)) {
                Server server = psm.getServer(serverName);
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
     * @param args The command arguments
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

            if (psm.serverExists(serverName)) {
                Server server = psm.getServer(serverName);
                if (!(server instanceof AMPServer)) {
                    return "§cServer " + serverName + " is not an AMP server!";
                }

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
     * @param args The command arguments
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
            if (psm.serverExists(serverName)) {
                Server server = psm.getServer(serverName);
                if (!(server instanceof AMPServer)) {
                    return "§cServer " + serverName + " is not an AMP server!";
                }

                Map<String, Object> status = server.getStatus();
                String state = (String) status.get("State");
                if (Objects.equals(state, "Ready")) {
                    String output = "§6" + serverName + ":\n";

                    // Player count
                    if (status.containsKey("Metrics")) {
                        HashMap<?, ?> metrics = (HashMap<?, ?>) status.get("Metrics");
                        if (metrics.containsKey("Active Users")) {
                            HashMap<?, ?> Players = (HashMap<?, ?>) metrics.get("Active Users");
                            int PlayersValue = (int) Math.round((double) Players.get("RawValue"));
                            int PlayersMax = (int) Math.round((double) Players.get("MaxValue"));
                            output += "§6Players: §9" + PlayersValue + "§6/§9" + PlayersMax + "\n";
                        }
                    }

                    // Player list
                    List<String> playerList = ((AMPServer) server).getPlayerList();
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

    /**
     * Server Command Handler
     * @param args The command arguments
     * @return The response
     */
    private String serverCommand(String[] args) {
        String message;
        String helpMessage = "§6Available subcommands:" +
                    "\nserver help - Show this message" +
                    "\nserver list - List available servers" +
                    "\nserver add <serverName> <panelName> <InstanceName> <InstanceId> - Add a server" +
                    "\nserver remove <serverName> - Remove a server";
        if (args.length == 1) {
            return "§cUsage: /psm server <subcommand>";
        }
        switch (args[1].toLowerCase()) {
            // server list
            case "list":
                message = "§6Available servers: §5\n" + String.join("\n", psm.getServers());
                break;
            // server add <serverName> <panelName> <InstanceName> <InstanceId>
            case "add":
                if (args.length >= 5) {
                    String serverName = args[2];
                    String panelName = args[3];
                    String instanceName = args[4];
                    String instanceId = args.length == 6 ? args[5] : null;
                    if (!psm.panelExists(panelName)) {
                        message = "§cPanel " + panelName + " does not exist!";
                        break;
                    }
                    if (psm.serverExists(serverName)) {
                        message = "§cServer " + serverName + " already exists!";
                        break;
                    }
                    Panel panel = psm.getPanel(panelName);
                    Server server = null;
                    switch (panel.getPanelType().toLowerCase()) {
                        case "ampstandalone":
                            // TODO: Implement AMP Standalone
                            break;
                        case "cubecodersamp":
                            AMPAPIHandler instanceAPI = ((AMPPanel) panel).getInstanceAPI(serverName, instanceName, instanceId);
                            server = new AMPServer(serverName, panelName, instanceName, instanceId, instanceAPI);
                    }
                    // Check if server is online
                    if (server != null && server.isOnline()) {
                        psm.setServer(serverName, server);
                        psm.saveServerConfig(serverName);
                        message = "§aServer " + serverName + " added!";
                    } else {
                        message = "§cServer " + serverName + " is offline!";
                    }
                } else {
                    message = "§cUsage: /psm server add <serverName> <panelName> <InstanceName> <InstanceId>";
                }
                break;

            // server edit <serverName> <panelName> <InstanceName> <InstanceId>
            case "edit":
                if (args.length >= 5) {
                    String serverName = args[2];
                    String panelName = args[3];
                    String instanceName = args[4];
                    String instanceId = args.length == 6 ? args[5] : null;
                    if (!psm.panelExists(panelName)) {
                        message = "§cPanel " + panelName + " does not exist!";
                        break;
                    }
                    if (!psm.serverExists(serverName)) {
                        message = "§cServer " + serverName + " does not exist!";
                        break;
                    }
                    Panel panel = psm.getPanel(panelName);
                    Server server = null;
                    switch (panel.getPanelType().toLowerCase()) {
                        case "ampstandalone":
                            // TODO: Implement AMP Standalone
                            break;
                        case "cubecodersamp":
                            AMPAPIHandler instanceAPI = ((AMPPanel) panel).getInstanceAPI(serverName, instanceName, instanceId);
                            server = new AMPServer(serverName, panelName, instanceName, instanceId, instanceAPI);
                    }
                    // Check if server is online
                    if (server != null && server.isOnline()) {
                        // Remove old server
                        psm.removeServer(serverName);
                        psm.deleteServerConfig(serverName);

                        // Add edited server
                        psm.setServer(serverName, server);
                        psm.saveServerConfig(serverName);
                        message = "§aServer " + serverName + " edited!";
                    } else {
                        message = "§cServer " + serverName + " is offline!";
                    }
                } else {
                    message = "§cUsage: /psm server edit <serverName> <panelName> <InstanceName> <InstanceId>";
                }
                break;

            // server remove <serverName>
            case "remove":
                if (args.length == 3) {
                    String serverName = args[2];
                    if (psm.serverExists(serverName)) {
                        psm.removeServer(serverName);
                        psm.deleteServerConfig(serverName);
                        message = "§aServer " + serverName + " removed!";
                    } else {
                        message = "§cServer " + serverName + " does not exist!";
                    }
                } else {
                    message = "§cUsage: /psm server remove <serverName>";
                }
                break;
            default:
                message = helpMessage;
                break;
        }
        return message;
    }

    /**
     * Group Command Handler
     * @param args The command arguments
     * @return The response
     */
    private String groupCommand(String[] args) {
        String message;
        String helpMessage = "§6Available subcommands:" +
                "\ngroup help - Show this message" +
                "\ngroup server list <groupName>" +
                "\ngroup server add <serverName> <groupName>" +
                "\ngroup server remove <serverName> <groupName>";
        if (args.length == 1) {
            return helpMessage;
        }
        switch (args[1].toLowerCase()) {
            // group list
            case "list":
                message = "§6Available groups: §5\n" + String.join("\n", psm.getGroups());
                break;
            // group server
            case "server":
                switch(args[2].toLowerCase()) {
                    // group server list
                    case "list":
                        if (args.length == 4) {
                            String groupName = args[3];
                            if (psm.groupExists(groupName)) {
                                Group group = psm.getGroup(groupName);
                                message = "§6Servers in group " + groupName + ": §5\n" + String.join("\n", group.getServers());
                            } else {
                                message = "§cGroup " + groupName + " does not exist!";
                            }
                        } else {
                            message = "§cUsage: /psm group server list <groupName>";
                        }
                        break;
                    // group server add <serverName> <groupName>
                    case "add":
                        if (args.length == 5) {
                            String groupName = args[4];
                            String serverName = args[3];
                            if (psm.groupExists(groupName)) {
                                if (psm.serverExists(serverName)) {
                                    Group group = psm.getGroup(groupName);
                                    if (!group.containsServer(serverName)) {
                                        group.addServer(serverName);
                                        psm.saveGroupServers(groupName);
                                        message = "§aAdded server " + serverName + " to group " + groupName + "!";
                                    } else {
                                        message = "§cServer " + serverName + " is already in group " + groupName + "!";
                                    }
                                } else {
                                    message = "§cServer " + serverName + " does not exist!";
                                }
                            } else {
                                message = "§cGroup " + groupName + " does not exist!";
                            }
                        } else {
                            message = "§cUsage: /psm group server add <serverName> <groupName>";
                        }
                        break;
                    // group server remove <serverName> <groupName>
                    case "remove":
                        if (args.length == 5) {
                            String groupName = args[4];
                            String serverName = args[3];
                            if (psm.groupExists(groupName)) {
                                if (psm.serverExists(serverName)) {
                                    Group group = psm.getGroup(groupName);
                                    if (group.containsServer(serverName)) {
                                        group.removeServer(serverName);
                                        psm.saveGroupServers(groupName);
                                        message = "§aRemoved server " + serverName + " from group " + groupName + "!";
                                    } else {
                                        message = "§cServer " + serverName + " is not in group " + groupName + "!";
                                    }
                                } else {
                                    message = "§cServer " + serverName + " does not exist!";
                                }
                            } else {
                                message = "§cGroup " + groupName + " does not exist!";
                            }
                        } else {
                            message = "§cUsage: /psm group server remove <serverName> <groupName>";
                        }
                        break;
                    default:
                        message = helpMessage;
                        break;
                }
                return message;
            // group task
            case "task":
                switch (args[2].toLowerCase()) {
                    // group task list
                    case "list":
                        if (args.length == 4) {
                            String groupName = args[3];
                            if (psm.groupExists(groupName)) {
                                Group group = psm.getGroup(groupName);
                                message = "§6Tasks in group " + groupName + ": §5\n" + String.join("\n", group.getTasks());
                            } else {
                                message = "§cGroup " + groupName + " does not exist!";
                            }
                        } else {
                            message = "§cUsage: /psm group task list <groupName>";
                        }
                        break;
                    // group task create <group> <task> <command> <interval>
                    case "create":
                    // group task remove <group> <task>
                    case "remove":
                        message = "§cNot implemented yet!";
                        break;
                    default:
                        message = "§6Available subcommands:" +
                                "\nhelp - Show this message" +
                                "\ngroup task list <groupName>" +
                                "\ngroup task add <taskName> <groupName>" +
                                "\ngroup task remove <taskName> <groupName>";
                        break;
                }
                return message;
            // group findplayer <groupName> <playerName>
            case "findplayer":
                if (args.length == 4) {
                    String groupName = args[2];
                    String playerName = args[3];
                    if (!psm.groupExists(groupName)) {
                        message = "§cGroup " + groupName + " does not exist!";
                        break;
                    }
                    Group group = psm.getGroup(groupName);
                    String serverName = group.findPlayer(playerName);
                    if (serverName.equals("")) {
                        message = "§cPlayer " + playerName + " is not on any server in group " + groupName + "!";
                        break;
                    }
                    message = "§6Player " + playerName + " is on server " + serverName + "!";
                } else {
                    message = "§cUsage: /psm group findplayer <groupName> <playerName>";
                }
                break;
            default:
                message = "§6Available subcommands:" +
                        "\nhelp - Show this message" +
                        "\ngroup list - List available groups" +
                        "\ngroup findplayer <groupName> <playerName> - Find player in group" +
                        "\ngroup server <command> - Manage group servers";
                break;
        }
        return message;
    }

    /**
     * Help Handler
     * @return The response
     */
    private String helpHandler() {
        return "§6Available commands:" +
                "\nhelp - Show this message" +
                "\nexit - Exit the application (CLI only)" +
                "\nstart <server> - Start server" +
                "\nstop <server> - Stop server" +
                "\nrestart <server> - Restart server" +
                "\nkill <server> - Kill server" +
                "\nsend <server> <command> - Send command to server" +
                "\nstatus <server> - Get server status" +
                "\n\nAMP Only:" +
                "\nsleep <server> - Put server to sleep" +
                "\nbackup <server> [name] [description] [sticky <- true or false] - Backup server" +
                "\nplayers <server> - Get server player list" +
                "\n\nOther Commands:" +
                "\nserver list - List available servers" +
                "\ngroup list - List available groups" +
                "\ngroup server list <groupName>" +
                "\ngroup findplayer <groupName> <playerName>" +
                "\ngroup server add <serverName> <groupName> - Add server to group" +
                "\ngroup server remove <serverName> <groupName> - Remove server from group";
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
                // Server Command Tree
                case "server":
                    message = serverCommand(args);
                    break;
                // Group Command Tree
                case "group":
                    message = groupCommand(args);
                    break;
                // Help
                default:
                    message = helpHandler();
                    break;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while executing the command!\n" + e.getMessage());
            e.printStackTrace();
            Server server = psm.getServer(args[1]);
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
