package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinTask;

public class Task {
    private final String name;
    private final String command;
    private final long interval;

    private final ArrayList<String> servers = new ArrayList<>();

    private final ArrayList<Condition> conditions = new ArrayList<>();

    private ForkJoinTask<Object> runningTask;

    // Constructor
    public Task(String name, String command, long interval, ArrayList<String> servers, ArrayList<Condition> conditions) {
        this.name = name;
        this.command = command;
        this.interval = interval;
        this.servers.addAll(servers);
        this.conditions.addAll(conditions);
    }

    // Get Name
    public String getName() {
        return name;
    }

    // Get Command
    public String getCommand() {
        return command;
    }

    // Get Interval
    public long getInterval() {
        return interval;
    }

    // Set Task
    public void setTask(ForkJoinTask<Object> task) {
        runningTask = task;
    }

    // Cancel Task
    public void cancelTask() {
        runningTask.cancel(true);
    }

    // Check Player Count
    public boolean checkPlayerCount(String serverName, Condition condition) {
        // Get player count
        Map<?, ?> status = PanelServerManager.getInstance().getServer(serverName).getStatus();
        if (!status.containsKey("PlayersValue")) {
            return false;
        }
        int playerCount = (int) status.get("PlayersValue");

        switch (condition.operator) {
            case "<":
                return playerCount < (int) condition.value;
            case ">":
                return playerCount > (int) condition.value;
            case "=":
            case "==":
                return playerCount == (int) condition.value;
            case "<=":
                return playerCount <= (int) condition.value;
            case ">=":
                return playerCount >= (int) condition.value;
            default:
                return false;
        }
    }

    // Check Conditions
    public boolean checkConditions(String serverName) {
        for (Condition condition : conditions) {
            switch (condition.placeholder) {
                case "playercount":
                    if (!checkPlayerCount(serverName, condition)) {
                        return false;
                    }
            }
        }
        return true;
    }
}
