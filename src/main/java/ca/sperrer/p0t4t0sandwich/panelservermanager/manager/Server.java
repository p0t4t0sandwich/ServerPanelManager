package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.HashMap;

public abstract class Server {
    public final String name;

    public Server(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    /**
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
    abstract public HashMap<String, Object> getStatus();
}