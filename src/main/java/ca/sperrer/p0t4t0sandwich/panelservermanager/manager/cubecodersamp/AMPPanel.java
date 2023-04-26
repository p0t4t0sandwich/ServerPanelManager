package ca.sperrer.p0t4t0sandwich.panelservermanager.manager.cubecodersamp;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.Panel;
import ca.sperrer.p0t4t0sandwich.panelservermanager.manager.PanelServerManager;

import java.util.ArrayList;
import java.util.Map;

public class AMPPanel extends Panel<AMPAPIHandler> {
    private Map<?, ?> loginResult;

    public AMPPanel(String name, String host, String username, String password) {
        super(name, host);
        this.API = new AMPAPIHandler(host, username, password, "", "");
        this.loginResult = API.Login();
    }

    @Override
    public boolean isOnline() {
        return (loginResult != null && (boolean) loginResult.get("success"));
    }

    @Override
    public boolean reLog() {
        this.loginResult = API.Login();
        return (loginResult != null && (boolean) loginResult.get("success"));
    }

    /**
     * Initialize the API object and login to the AMP instance.
     * Get the instance id and proxy the login through the ADS instance.
     * @return Whether the login was successful
     */
    public AMPAPIHandler getInstanceAPI(String serverName, String instanceName, String instanceId) {
        AMPAPIHandler InstanceAPI = null;
        if (instanceName != null) {
            // Get instance ID
            if (instanceId == null) {
                // Loop through the targets
                for (Map<String,Object> target : (ArrayList<Map<String,Object>>) API.ADSModule_GetInstances().get("result")) {

                    // Loop through the target instances
                    for (Map<String, Object> instance : (ArrayList<Map<String, Object>>) target.get("AvailableInstances")) {

                        // Grab the instance id
                        String name = (String) instance.get("InstanceName");
                        if (name.equals(instanceName)) {
                            if (instance.containsKey("InstanceID")) {
                                instanceId = ((String) instance.get("InstanceID")).split("-")[0];
                            }

                            // Save the id to the config
                            PanelServerManager.getInstance().addServerID(serverName, instanceId);
                            break;
                        }

                        // Break if the instance id is found
                        if (instanceId != null) {
                            break;
                        }
                    }
                }
            }
            InstanceAPI = API.InstanceLogin(instanceId);
        }
        return InstanceAPI;
    }
}