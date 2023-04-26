package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Task {
    private final String name;
    private final String command;
    private final int interval;

    private final List<String> servers = new ArrayList<>();

    private final List<Condition> conditions = new ArrayList<>();

    // Constructor
    public Task(String name, String command, int interval, List<String> servers, List<Condition> conditions) {
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
    public int getInterval() {
        return interval;
    }

    // Check Player Count
    public boolean checkPlayerCount(String serverName, Condition condition) {
        // Get player count
        HashMap<?, ?> status = PanelServerManager.getInstance().getServer(serverName).getStatus();
        if (!status.containsKey("Metrics")) {
            return false;
        }

        HashMap<String, Object> metrics = (HashMap<String, Object>) status.get("Metrics");
        if (!metrics.containsKey("Active Users")) {
            return false;
        }

        HashMap<String, Object> activeUsers = (HashMap<String, Object>) metrics.get("Active Users");
        int playerCount = (int) Math.round((double) activeUsers.get("RawValue"));

        switch (condition.operator) {
            case "<":
                return playerCount < (int) condition.value;
            case ">":
                return playerCount > (int) condition.value;
            case "=":
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
