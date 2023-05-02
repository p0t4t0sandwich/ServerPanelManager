package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.HashMap;
import java.util.concurrent.ForkJoinTask;

public class Task {
    /**
     * Properties of the Task class
     * name: The name of the task
     * command: The command to run
     * interval: The interval to run the task at
     * servers: The servers to run the task on
     * conditions: The conditions to check before running the task
     * runningTask: The thread running the task
     */
    private final String taskName;
    private final String command;
    private final long interval;

    private final HashMap<String, Condition> conditions = new HashMap<>();

    private ForkJoinTask<Object> runningTask;

    /**
     * Constructor for the Task class
     * @param taskName The name of the task
     * @param command The command to run
     * @param interval The interval to run the task at
     * @param conditions The conditions to check before running the task
     */
    public Task(String taskName, String command, long interval, HashMap<String, Condition> conditions) {
        this.taskName = taskName;
        this.command = command;
        this.interval = interval;
        this.conditions.putAll(conditions);
    }

    /**
     * Get the name of the task
     * @return The name of the task
     */
    public String getName() {
        return taskName;
    }

    /**
     * Get the command to run
     * @return The command to run
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get the interval to run the task at
     * @return The interval to run the task at
     */
    public long getInterval() {
        return interval;
    }

    /**
     * Get the conditions to check before running the task
     * @return The conditions to check before running the task
     */
    public HashMap<String, Condition> getConditions() {
        return conditions;
    }

    /**
     * Set Condition
     * @param conditionNumber The number of the condition
     * @param condition The condition to set
     */
    public void setCondition(int conditionNumber, Condition condition) {
        conditions.put(String.valueOf(conditionNumber), condition);
    }

    /**
     * Set Conditions
     * @param conditions The conditions to set
     */
    public void setConditions(HashMap<String, Condition> conditions) {
        this.conditions.clear();
        this.conditions.putAll(conditions);
    }

    /**
     * Get the thread running the task
     * @return The thread running the task
     */
    public ForkJoinTask<Object> getTask() {
        return runningTask;
    }

    /**
     * Set the thread running the task
     * @param task The thread running the task
     */
    public void setTask(ForkJoinTask<Object> task) {
        runningTask = task;
    }

    /**
     * Cancel the running task
     * @return Whether the task was cancelled
     */
    public boolean cancelTask() {
        return runningTask.cancel(true);
    }

    /**
     * Delete the task
     */
    public void deleteTask() {
        runningTask = null;
    }

    /**
     * Check the conditions for a server
     * @param serverName The name of the server
     * @return Whether the conditions are true
     */
    public boolean checkConditions(String serverName) {
        for (HashMap.Entry<String, Condition> conditionMap : conditions.entrySet()) {
            Condition condition = conditionMap.getValue();
            if (!condition.check(serverName)) {
                return false;
            }
        }
        return true;
    }
}
