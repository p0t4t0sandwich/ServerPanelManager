package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.Map;

public abstract class Server {
    /**
     * Properties of the Server class
     * serverName: The name of the server
     */
    public final String serverName;

    /**
     * Constructor for the Server class
     * @param serverName The name of the server
     */
    public Server(String serverName) {
        this.serverName = serverName;
    }

    /**
     * Get the name of the server
     * @return The name of the server
     */
    public String getName() {
        return serverName;
    }

    /**
     * Get the online status of the serverName.
     * @return Whether the server is online or not.
     */
    abstract public boolean isOnline();

    /**
     * Logs into the API again.
     * @return Whether the login was successful or not.
     */
    abstract public boolean reLog();

    /**
     * Starts the server.
     */
    abstract public void startServer();

    /**
     * Stops the server.
     */
    abstract public void stopServer();

    /**
     * Restarts the server.
     */
    abstract public void restartServer();

    /**
     * Kills the server.
     */
    abstract public void killServer();

    /**
     * Sends a command to the server.
     * @param message The command to send.
     */
    abstract public void sendCommand(String message);

    /**
     * @return The status of the server.
     */
    abstract public Map<String, Object> getStatus();
}