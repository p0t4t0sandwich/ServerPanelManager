package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.ArrayList;
import java.util.HashMap;

import static ca.sperrer.p0t4t0sandwich.panelservermanager.Utils.repeatTaskAsync;

public class Group {
    private final String groupName;
    private static final ArrayList<String> servers = new ArrayList<>();
    private static final HashMap<String, Task> tasks = new HashMap<>();
    private static final HashMap<String, Object> variableStore = new HashMap<>();

    // Constructor
    public Group(String groupName, ArrayList<String> s, HashMap<String, Task> t) {
        this.groupName = groupName;
        servers.addAll(s);
        tasks.putAll(t);
    }

    // Set Server
    public static void addServer(String serverName) {
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

    // Task exists
    public static boolean taskExists(String taskName) {
        return tasks.containsKey(taskName);
    }

    // Start task
    public static void startTask(String taskName) {
        if (!taskExists(taskName)) {
            return;
        }
        Task task = getTask(taskName);
        task.setTask(repeatTaskAsync(() -> {
            try {
                for (String serverName : servers) {
                    boolean result = task.checkConditions(serverName);
                    System.out.println("Result: " + result);
                    if (result) {
                        HashMap<String, Object> parseMap = new HashMap<>();
                        parseMap.put("server", serverName);
                        String command = task.getCommand();
                        String parsedCommand = parseCommand(parseMap, command);
                        //
                        System.out.println("Sending command: " + parsedCommand);
                        //
                        PanelServerManager.getInstance().commandMessenger(parsedCommand.split(" "));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while executing task: " + taskName);
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0L, 20L * task.getInterval()));
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

    // Command parser
    public static String parseCommand(HashMap<String, Object> parseMap, String command) {
        for (String key : parseMap.keySet()) {
            command = command.replace("{" + key + "}", parseMap.get(key).toString());
        }
        return command;
    }
}
