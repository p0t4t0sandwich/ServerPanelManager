package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.Map;

public class Condition {
    /**
     * Parameters for the Condition class
     * placeholder: The placeholder for the condition
     * operator: The operator for the condition
     * value: The value for the condition
     */
    public final String placeholder;
    public final String operator;
    public final Object value;

    /**
     * Constructor for the Condition class
     * @param placeholder The placeholder for the condition
     * @param operator The operator for the condition
     * @param value The value for the condition
     */
    public Condition(String placeholder, String operator, Object value) {
        this.placeholder = placeholder;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Check player count of server
     * @param serverName The name of the server
     * @return Whether the condition is true
     */
    public boolean checkPlayerCount(String serverName) {
        // Get player count
        Map<?, ?> status = PanelServerManager.getInstance().getServer(serverName).getStatus();
        if (!status.containsKey("PlayersValue")) {
            return false;
        }
        int playerCount = (int) status.get("PlayersValue");

        // Check condition
        switch (operator) {
            case "<":
                return playerCount < (int) value;
            case ">":
                return playerCount > (int) value;
            case "=":
            case "==":
                return playerCount == (int) value;
            case "<=":
                return playerCount <= (int) value;
            case ">=":
                return playerCount >= (int) value;
            default:
                return false;
        }
    }

    public boolean check(String serverName) {
        switch (placeholder) {
            case "playercount":
                if (!checkPlayerCount(serverName)) {
                    return false;
                }
        }
        return true;
    }
}