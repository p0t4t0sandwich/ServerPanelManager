package ca.sperrer.p0t4t0sandwich.panelservermanager.common;

import ca.sperrer.p0t4t0sandwich.panelservermanager.common.cubecodersamp.AMPServer;

import java.util.ArrayList;
import java.util.HashMap;

import static ca.sperrer.p0t4t0sandwich.panelservermanager.common.Utils.repeatTaskAsync;

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
    private static final HashMap<String, Task> tasks = new HashMap<>();
    private static final HashMap<String, Object> variableStore = new HashMap<>();

    /**
     * Constructor for the Group class
     * @param groupName The name of the group
     * @param s The servers in the group
     * @param t The tasks in the group
     */
    public Group(String groupName, ArrayList<String> s, HashMap<String, Task> t) {
        this.groupName = groupName;
        servers.addAll(s);
        tasks.putAll(t);
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
     * Get the tasks in the group
     * @return The tasks in the group
     */
    public ArrayList<String> getTasks() {
        return new ArrayList<>(tasks.keySet());
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
     * Get a Task from the group
     * @param taskName The name of the task to get
     * @return The specified task
     */
    public Task getTask(String taskName) {
        return tasks.get(taskName);
    }

    /**
     * Set a task in the group
     * @param taskName The name of the task to set
     * @param task The task to set
     */
    public void setTask(String taskName, Task task) {
        tasks.put(taskName, task);
    }

    /** Remove a task from the group
     * @param taskName The name of the task to remove
     */
    public void removeTask(String taskName) {
        tasks.remove(taskName);
    }

    /**
     * Check if the group contains a task
     * @param taskName The name of the task to check
     * @return Whether the group contains the task
     */
    public boolean containsTask(String taskName) {
        return tasks.containsKey(taskName);
    }

    /**
     * Check if the group contains a task
     * @param taskName The name of the task to check
     * @return Whether the group contains the task
     */
    public boolean taskExists(String taskName) {
        return tasks.containsKey(taskName);
    }

    /**
     * Start a task
     * @param taskName The name of the task to start
     */
    public void startTask(String taskName) {
        if (!taskExists(taskName)) {
            return;
        }
        Task task = getTask(taskName);
        task.setTask(repeatTaskAsync(() -> {
            try {
                for (String serverName : servers) {
                    boolean result = task.checkConditions(serverName);
                    if (result) {
                        HashMap<String, Object> parseMap = new HashMap<>();
                        parseMap.put("server", serverName);
                        String command = task.getCommand();
                        String parsedCommand = parseCommand(parseMap, command);
                        PanelServerManager.getInstance().commandHandler.commandMessenger(parsedCommand.split(" "));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while executing task: " + taskName);
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0L, 20L * task.getInterval()));
    }

    /**
     * Stop a task
     * @param taskName The name of the task to stop
     * @return Whether the task was stopped
     */
    public boolean stopTask(String taskName) {
        if (!taskExists(taskName)) {
            return false;
        }
        Task task = getTask(taskName);
        boolean success = task.cancelTask();
        task.deleteTask();
        return success;
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
            Server server = PanelServerManager.getInstance().getServer(serverName);
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
