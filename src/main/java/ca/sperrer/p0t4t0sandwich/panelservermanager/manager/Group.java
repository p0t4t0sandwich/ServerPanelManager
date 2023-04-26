package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Group {
    private static final List<String> servers = new ArrayList<>();
    private static final HashMap<String, Task> tasks = new HashMap<>();
    private static final HashMap<String, Object> variableStore = new HashMap<>();

    // Constructor
    public Group(List<String> s, HashMap<String, Task> t) {
        servers.addAll(s);
        tasks.putAll(t);
    }

    // Set Server
    public static void setServer(String serverName) {
        servers.add(serverName);
    }

    // Remove Server
    public static void removeServer(String serverName) {
        servers.remove(serverName);
    }

    // Contains Server
    public static boolean containsServer(String serverName) {
        return servers.contains(serverName);
    }

    // Get task
    public static Task getTask(String taskName) {
        return tasks.get(taskName);
    }

    // Set task
    public static void setTask(String taskName, Task task) {
        tasks.put(taskName, task);
    }

    // Remove task
    public static void removeTask(String taskName) {
        tasks.remove(taskName);
    }

    // Contains task
    public static boolean containsTask(String taskName) {
        return tasks.containsKey(taskName);
    }

    // Get variable
    public static Object getVariable(String variableName) {
        return variableStore.get(variableName);
    }

    // Set variable
    public static void setVariable(String variableName, Object value) {
        variableStore.put(variableName, value);
    }

    // Remove variable
    public static void removeVariable(String variableName) {
        variableStore.remove(variableName);
    }

    // Contains variable
    public static boolean containsVariable(String variableName) {
        return variableStore.containsKey(variableName);
    }
}
