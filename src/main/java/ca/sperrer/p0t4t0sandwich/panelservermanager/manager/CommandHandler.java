package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.cubecodersamp.AMPPanel;
import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.cubecodersamp.AMPServer;

import java.util.*;

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
            return "§cUsage: /psm players <serverName>";
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
                    if (status.containsKey("PlayersValue")) {
                        int PlayersValue = (int) status.get("PlayersValue");
                        int PlayersMax = (int) status.get("PlayersMax");
                        output += "§6Players: §9" + PlayersValue + "§6/§9" + PlayersMax + "\n";
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
     * Find Player Handler
     * @param args The command arguments
     * @return The response
     */
    private String findPlayerHandler(String[] args) {
        // Find player
        if (args.length == 2) {
            String playerName = args[1];
            List<String> groups = psm.getGroups();
            for (String group : groups) {
                String server = psm.getGroup(group).findPlayer(playerName);
                if (!server.equals("")) {
                    return "§6Player " + playerName + " is online on: §5" + server;
                }
            }
            return "§cCould not find player " + playerName + "!";
        } else {
            return "§cUsage: /psm find <player>";
        }
    }

    /**
     * Panel Command Handler
     * @param args The command arguments
     * @return The response
     */
    private String panelCommand(String[] args) {
        String message;
        String helpMessage = "§6Available subcommands:" +
                "\npanel help - Show this message" +
                "\npanel list - List available servers" +
                "\npanel add <panelName> <type> <host> <username> <password> - Add a panel" +
                "\npanel edit <panelName> <type> <host> <username> <password> - Edit a panel" +
                "\npanel remove <panelName> - Remove a panel";
        if (args.length == 1) {
            return "§cUsage: /psm panel <subcommand>";
        }
        switch (args[1].toLowerCase()) {
            // panel list
            case "list":
                message = "§6Available panels: §5\n" + String.join("\n", psm.getPanels());
                break;
            // panel add <panelName> <type> <host> <username> <password>
            case "add":
                if (args.length == 7) {
                    String panelName = args[2];
                    String type = args[3];
                    String host = args[4];
                    String username = args[5];
                    String password = args[6];

                    if (psm.panelExists(panelName)) {
                        message = "§cPanel " + panelName + " already exists!";
                        break;
                    }
                    Panel panel = null;
                    switch (type) {
                        case "cubecodersamp":
                            panel = new AMPPanel(panelName, host, username, password);
                            psm.setPanel(panelName, panel);
                    }
                    // Check if server is online
                    if (panel != null && panel.isOnline()) {
                        psm.setPanel(panelName, panel);
                        psm.savePanelConfig(panelName);
                        message = "§aServer " + panelName + " added!";
                    } else {
                        message = "§cServer " + panelName + " is offline!";
                    }
                } else {
                    message = "§cUsage: /psm panel add <panelName> <type> <host> <username> <password>";
                }
                break;
            // panel remove <panelName>
            case "remove":
                if (args.length == 3) {
                    String panelName = args[2];
                    if (psm.panelExists(panelName)) {
                        psm.removePanel(panelName);
                        psm.deletePanelConfig(panelName);
                        message = "§aPanel " + panelName + " removed!";
                    } else {
                        message = "§cPanel " + panelName + " does not exist!";
                    }
                } else {
                    message = "§cUsage: /psm panel remove <panelName>";
                }
                break;
            // panel edit <panelName> <type> <host> <username> <password>
            case "edit":
                if (args.length == 7) {
                    String panelName = args[2];
                    String type = args[3];
                    String host = args[4];
                    String username = args[5];
                    String password = args[6];

                    if (!psm.panelExists(panelName)) {
                        message = "§cPanel " + panelName + " does not exist!";
                        break;
                    }
                    Panel panel = null;
                    switch (type) {
                        case "cubecodersamp":
                            panel = new AMPPanel(panelName, host, username, password);
                            psm.setPanel(panelName, panel);
                    }
                    // Check if server is online
                    if (panel != null && panel.isOnline()) {
                        // Remove old panel
                        psm.removeServer(panelName);
                        psm.deleteServerConfig(panelName);

                        // Add edited panel
                        psm.setPanel(panelName, panel);
                        psm.savePanelConfig(panelName);
                        message = "§aPanel " + panelName + " edited!";
                    } else {
                        message = "§cPanel " + panelName + " is offline!";
                    }
                } else {
                    message = "§cUsage: /psm panel edit <panelName> <type> <host> <username> <password>";
                }
                break;
            default:
                message = helpMessage;
                break;
        }
        return message;
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
                    "\nserver remove <serverName> - Remove a server" +
                    "\nserver edit <serverName> <panelName> <InstanceName> <InstanceId> - Edit a server";
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
                "\ngroup list - List available groups" +
                "\ngroup find <group> <player> - Find what server a player is on" +
                "\ngroup command <group> <command> - Run a command on all servers in a group" +
                "\ngroup server <subcommand> - Manage servers in a group" +
                "\ngroup task <subcommand> - Manage tasks in a group";
        if (args.length == 1) {
            return helpMessage;
        }
        switch (args[1].toLowerCase()) {
            // group list
            case "list":
                message = "§6Available groups: §5\n" + String.join("\n", psm.getGroups());
                break;
            // group find <groupName> <playerName>
            case "find":
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
                    message = "§cUsage: /psm group find <groupName> <playerName>";
                }
                break;
            // group command <groupName> <command>
            case "command":
                if (args.length >= 4) {
                    String groupName = args[2];
                    if (!psm.groupExists(groupName)) {
                        message = "§cGroup " + groupName + " does not exist!";
                        break;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    Group group = psm.getGroup(groupName);
                    ArrayList<String> servers = group.getServers();
                    for (String serverName : servers) {
                        String[] command = new String[args.length - 2];
                        command[0] = args[3];
                        command[1] = serverName;
                        if (args.length > 4) {
                            System.arraycopy(args, 4, command, 2, args.length - 4);
                        }
                        stringBuilder.append(commandMessenger(command));
                    }
                    message = stringBuilder.toString();
                } else {
                    message = "§cUsage: /psm group command <groupName> <command>";
                }
                break;
            // group players <groupName>
            case "players":
                if (args.length == 3) {
                    String groupName = args[2];
                    if (!psm.groupExists(groupName)) {
                        message = "§cGroup " + groupName + " does not exist!";
                        break;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    Group group = psm.getGroup(groupName);
                    ArrayList<String> servers = group.getServers();
                    for (String serverName : servers) {
                        stringBuilder.append("§6Players on server ")
                                .append(playerListHandler(new String[]{"players", serverName}))
                                .append("\n");
                    }
                    message = stringBuilder.toString();
                } else {
                    message = "§cUsage: /psm group players <groupName>";
                }
                break;
            // group server <subcommand>
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
                                    message =  "§cServer " + serverName + " does not exist!";
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
                                        message =  "§cServer " + serverName + " is not in group " + groupName + "!";
                                    }
                                } else {
                                    message =  "§cServer " + serverName + " does not exist!";
                                }
                            } else {
                                message =  "§cGroup " + groupName + " does not exist!";
                            }
                        } else {
                            message = "§cUsage: /psm group server remove <serverName> <groupName>";
                        }
                        break;
                    default:
                        message = "§6Available subcommands:" +
                                "\ngroup server help - Show this message" +
                                "\ngroup server list <groupName> - List servers in a group" +
                                "\ngroup server add <serverName> <groupName> - Add a server to a group" +
                                "\ngroup server remove <serverName> <groupName> - Remove a server from a group";
                        break;
                }
                return message;
            // group task <subcommand>
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
                    // group task create <groupName> <taskName> <command> <interval>
                    case "create":
                    // group task remove <groupName> <taskName>
                    case "remove":
                        message = "§cNot implemented yet!";
                        break;
                    default:
                        message = "§6Available subcommands:" +
                                "\ngroup task help - Show this message" +
                                "\ngroup task list <groupName>" +
                                "\ngroup task add <taskName> <groupName>" +
                                "\ngroup task remove <taskName> <groupName>";
                        break;
                }
                return message;
            default:
                message = "§6Available subcommands:" +
                        "\nhelp - Show this message" +
                        "\ngroup list - List available groups" +
                        "\ngroup find <groupName> <playerName> - Find player in group" +
                        "\ngroup task <subcommand> - Manage group tasks" +
                        "\ngroup server <subcommand> - Manage group servers";
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
                "\nfind <player> - Find the server the player is on" +
                "\n\nOther Commands:" +
                "\npanel <subcommand>" +
                "\nserver <subcommand>" +
                "\ngroup <subcommand>";
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
                // start <serverName>
                case "start":
                    message = startServerHandler(args);
                    break;
                // stop <serverName>
                case "stop":
                    message = stopServerHandler(args);
                    break;
                // restart <serverName>
                case "restart":
                    message = restartServerHandler(args);
                    break;
                // kill <serverName>
                case "kill":
                    message = killServerHandler(args);
                    break;
                // sleep <serverName>
                case "sleep":
                    message = sleepServerHandler(args);
                    break;
                // send <serverName> <message>
                case "send":
                    message = sendCommandHandler(args);
                    break;
                // status <serverName>
                case "status":
                    message = getStatusHandler(args);
                    break;
                // backup <serverName> [name] [description] [sticky <- true or false]
                case "backup":
                    message = backupServerHandler(args);
                    break;
                // players <serverName>
                case "players":
                    message = playerListHandler(args);
                    break;
                // find <serverName>
                case "find":
                    message = findPlayerHandler(args);
                    break;
                // panel <subcommand>
                case "panel":
                    message = panelCommand(args);
                    break;
                // server <subcommand>
                case "server":
                    message = serverCommand(args);
                    break;
                // group <subcommand>
                case "group":
                    message = groupCommand(args);
                    break;
                // help
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
