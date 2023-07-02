package dev.neuralnexus.serverpanelmanager.common;

import dev.neuralnexus.serverpanelmanager.common.cubecodersamp.AMPServer;

import java.util.ArrayList;
import java.util.HashMap;

import static dev.neuralnexus.serverpanelmanager.common.Utils.repeatTaskAsync;

public class Group {
    /**
     * Properties of the Group class
     * groupName: The name of the group
     * servers: The servers in the group
     * tasks: The tasks in the group
     * variableStore: The variables in the group
     */
    private final String groupName;
    private static final ArrayList<String> servers = new ArrayList<>();
    private static final HashMap<String, Object> variableStore = new HashMap<>();

    /**
     * Constructor for the Group class
     * @param groupName The name of the group
     * @param s The servers in the group
     */
    public Group(String groupName, ArrayList<String> s) {
        this.groupName = groupName;
        servers.addAll(s);
    }

    /**
     * Get the name of the group
     * @return The name of the group
     */
    public String getName() {
        return groupName;
    }

    /**
     * Get the servers in the group
     * @return The servers in the group
     */
    public ArrayList<String> getServers() {
        return servers;
    }

    /**
     * Add server to the group
     * @param serverName The name of the server to add
     */
    public void addServer(String serverName) {
        servers.add(serverName);
    }

    /**
     * Remove server from the group
     * @param serverName The name of the server to remove
     */
    public void removeServer(String serverName) {
        servers.remove(serverName);
    }

    /**
     * Check if the group contains a server
     * @param serverName The name of the server to check
     * @return Whether the group contains the server
     */
    public boolean containsServer(String serverName) {
        return servers.contains(serverName);
    }

    /**
     * Get variable
     * @param variableName The name of the variable to get
     * @return The specified variable
     */
    public Object getVariable(String variableName) {
        return variableStore.get(variableName);
    }

    /**
     * Set variable
     * @param variableName The name of the variable to set
     * @param value The value to set the variable to
     */
    public void setVariable(String variableName, Object value) {
        variableStore.put(variableName, value);
    }

    /**
     * Remove variable
     * @param variableName The name of the variable to remove
     */
    public void removeVariable(String variableName) {
        variableStore.remove(variableName);
    }

    /**
     * Check if the group contains a variable
     * @param variableName The name of the variable to check
     * @return Whether the group contains the variable
     */
    public boolean containsVariable(String variableName) {
        return variableStore.containsKey(variableName);
    }

    /**
     * Parse a command
     * @param parseMap The map of variables to parse
     * @param command The command to parse
     * @return The parsed command
     */
    public static String parseCommand(HashMap<String, Object> parseMap, String command) {
        for (String key : parseMap.keySet()) {
            command = command.replace("{" + key + "}", parseMap.get(key).toString());
        }
        return command;
    }

    /**
     * Find Player command
     * @param playerName The name of the player to find
     * @return The server the player is on
     */
    public String findPlayer(String playerName) {
        for (String serverName : getServers()) {
            Server server = ServerPanelManager.getInstance().getServer(serverName);
            if (server instanceof AMPServer) {
                AMPServer ampServer = (AMPServer) server;
                if (ampServer.getPlayerList().contains(playerName)) {
                    return serverName;
                }
            }
        }
        return "";
    }
}
