package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    // Check Player Count
    public boolean checkPlayerCount(String serverName, Condition condition) {
        // Get player count
        Map<?, ?> status = PanelServerManager.getInstance().getServer(serverName).getStatus();
        if (!status.containsKey("Metrics")) {
            return false;
        }

        Map<String, Object> metrics = (Map<String, Object>) status.get("Metrics");
        if (!metrics.containsKey("Active Users")) {
            return false;
        }

        Map<String, Object> activeUsers = (Map<String, Object>) metrics.get("Active Users");
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
