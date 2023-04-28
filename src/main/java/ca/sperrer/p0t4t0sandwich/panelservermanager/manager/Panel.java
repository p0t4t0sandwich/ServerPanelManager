package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

public abstract class Panel<T> {
    /**
     * Properties of the Panel class
     * panelName: The name of the panel
     * host: The host address of the panel
     * API: The API of the panel
     */
    private final String panelName;
    private final String host;
    public T API;

    /**
     * Constructor for the Panel class
     * @param panelName The name of the panel
     * @param host The host address of the panel
     */
    public Panel(String panelName, String host) {
        this.panelName = panelName;
        this.host = host;
    }

    /**
     * Get the online status of the panel
     * @return The online status of the panel
     */
    abstract public boolean isOnline();

    /**
     * Re-log into the panel if the session has expired
     * @return The success of the re-log
     */
    abstract public boolean reLog();
}
