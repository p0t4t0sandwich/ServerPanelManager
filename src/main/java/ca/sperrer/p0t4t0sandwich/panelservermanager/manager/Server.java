package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

import java.util.List;
import java.util.Map;

public abstract class Server {
    public final String name;

    public Server(String name) {
        this.name = name;
    }

    abstract public boolean isOnline();
    abstract public void startServer();
    abstract public void stopServer();
    abstract public void restartServer();
    abstract public void killServer();
    abstract public void sendCommand(String message);
    abstract public Map<String, Object> getStatus();
    abstract public List<String> getPlayerList();
    abstract public boolean reLog();
}