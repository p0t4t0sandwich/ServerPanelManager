package ca.sperrer.p0t4t0sandwich.panelservermanager.manager;

public abstract class Panel<T> {
    private final String name;
    private final String host;

    public T API;

    public Panel(String name, String host) {
        this.name = name;
        this.host = host;
    }

    abstract public boolean isOnline();
    abstract public boolean reLog();
}
