package ca.sperrer.p0t4t0sandwich.ampservermanager;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;

public class Instance {
    public String name;
    public String id;
    public AMPAPIHandler API;

    public Instance(String name, String id, AMPAPIHandler API) {
        this.name = name;
        this.id = id;
        this.API = API;
    }
}