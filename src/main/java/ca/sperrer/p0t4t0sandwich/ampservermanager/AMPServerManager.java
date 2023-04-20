package ca.sperrer.p0t4t0sandwich.ampservermanager;

import ca.sperrer.p0t4t0sandwich.ampapi.AMPAPIHandler;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

public class AMPServerManager {
    public YamlDocument config;
    public Object logger;
    public String host;
    public String username;
    public String password;
    public static AMPAPIHandler ADS;
    public static HashMap<String, Instance> instances = new HashMap<>();
    private static AMPServerManager singleton;
    public static AMPServerManager getInstance() {
        return singleton;
    }

    // Run async task
    public static void runTaskAsync(Runnable run) {
        ForkJoinPool.commonPool().submit(run);
    }

    // Repeat async task
    public static void repeatTaskAsync(Runnable run, Long delay, Long period) {
        ForkJoinPool.commonPool().submit(() -> {
            try {
                Thread.sleep(delay*1000/20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    Thread.sleep(period*1000/20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                run.run();
            }
        });
    }

    // Constructor
    public AMPServerManager(String configPath, Object logger) {
        singleton = this;
        this.logger = logger;

        // Config
        try {
            config = YamlDocument.create(new File("./" + configPath + "/AMPServerManager", "config.yml"),
                    Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("config.yml"))
            );
            config.reload();

            // Get AMP host, username, and password
            host = config.getString("amp.host");
            username = config.getString("amp.username");
            password = config.getString("amp.password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Instance type
    public static class Instance {
        public String name;
        public String id;
        public AMPAPIHandler API;

        public Instance(String name, String id, AMPAPIHandler API) {
            this.name = name;
            this.id = id;
            this.API = API;
        }
    }

    // Variable Logger handler
    public void useLogger(Object logger, String message) {
        if (logger instanceof java.util.logging.Logger) {
            ((java.util.logging.Logger) logger).info(message);
        } else if (logger instanceof org.slf4j.Logger) {
            ((org.slf4j.Logger) logger).info(message);
        } else if (logger instanceof org.apache.logging.log4j.Logger) {
            ((org.apache.logging.log4j.Logger) logger).info(message);
        }
    }

    // Init AMPAPIHandler
    public void init() {
        ADS = new AMPAPIHandler(host, username, password, "", "");
        ADS.Login();

        // Get and initialize instances
        Map<String, Object> serverConfig = (Map<String, Object>) config.getBlock("servers").getStoredValue();
        for (Map.Entry<String, Object> entry: serverConfig.entrySet()) {
            // Get instance name and id
            String serverName = entry.getKey();
            String name = config.getString("servers." + serverName + ".name");
            String id = config.getString("servers." + serverName + ".id");

            Instance instance = new Instance(name, id, null);
            boolean status = instanceLogin(instance);
            if (status) {
                useLogger(logger, "Instance " + instance.name + " is online!");
            } else {
                useLogger(logger, "Instance " + instance.name + " is offline!");
            }
        }

        // Initialize groups
        Map<String, Object> groupConfig = (Map<String, Object>) config.getBlock("groups").getStoredValue();
        for (Map.Entry<String, Object> entry: groupConfig.entrySet()) {
            // Get group name and servers
            String groupName = entry.getKey();
            ArrayList<String> servers = (ArrayList<String>) config.getBlock("groups." + groupName + ".servers").getStoredValue();

            // Loop through tasks
        }
    }

    // Start AMPAPIHandler
    public void start() {
        runTaskAsync(this::init);
    }

    // Add instance to instances
    public boolean instanceLogin(Instance instance) {
        if (instance.name != null) {
            // Get instance ID
            if (instance.id == null) {
                // Loop through the targets
                for (Map<String,Object> target : (ArrayList<Map<String,Object>>) ADS.ADSModule_GetInstances().get("result")) {

                    // Loop through the target instances
                    for (Map<String, Object> inst : (ArrayList<Map<String, Object>>) target.get("AvailableInstances")) {
                        String instanceModule = (String) inst.get("Module");

                        // Check if the instance is a Minecraft instance and grab the instance id
                        if (instanceModule.equals("Minecraft")) {
                            String instanceName = (String) inst.get("InstanceName");
                            if (instanceName.equals(instance.name)) {
                                String id = (String) inst.get("InstanceID");
                                config.set("servers." + instance.name + ".id", id);

                                // Save the id to the config
                                try {
                                    config.save();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                instance.id = id;
                                break;
                            }
                        }
                        
                        // Break if the instance id is found
                        if (instance.id != null) {
                            break;
                        }
                    }
                }
            }

            instance.API = ADS.InstanceLogin(instance.id);
            if (instance.API != null) {
                Map<? extends Object, ? extends Object> status = instance.API.Login();
                if (status.get("success").equals(true)) {
                    instances.put(instance.name, instance);
                    return true;
                }
            }
        }
        return false;
    }

    // General Instance Method Wrapper
    public Map<String, Object> instanceMethod(String serverName, Function<String[], Map<String, Object>> method) {
        try {
            if (!instances.containsKey(serverName)) {
                // Get instance name and id
                String name = config.getString("servers." + serverName + ".name");
                String id = config.getString("servers." + serverName + ".id");

                // Login to instance
                boolean success = instanceLogin(new Instance(name, id, null));
                if (!success) {
                    return null;
                }
            }
            return method.apply(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Start Server
    public void startServer(String serverName) {
        instanceMethod(serverName, (args) -> instances.get(serverName).API.Core_Start());
    }

    // Stop Server
    public void stopServer(String serverName) {
        instanceMethod(serverName, (args) -> instances.get(serverName).API.Core_Stop());
    }

    // Restart Server
    public void restartServer(String serverName) {
        instanceMethod(serverName, (args) -> instances.get(serverName).API.Core_Restart());
    }

    // Kill Server
    public void killServer(String serverName) {
        instanceMethod(serverName, (args) -> instances.get(serverName).API.Core_Kill());
    }

    // Sleep Server
    public void sleepServer(String serverName) {
        instanceMethod(serverName, (args) -> instances.get(serverName).API.Core_Sleep());
    }

    // Send Command
    public void sendCommand(String serverName, String command) {
        instanceMethod(serverName, (args) -> instances.get(serverName).API.Core_SendConsoleMessage(command));
    }

    // Get Status
    public Map<String, Object> getStatus(String serverName) {
        return instanceMethod(serverName, (args) -> instances.get(serverName).API.Core_GetStatus());
    }

    // Parse Status
    private Map<String, Object> parseStatus(String serverName) {
        Map<String, Object> status = getStatus(serverName);
        if (status == null) {
            return null;
        }

        Map<String, Object> newStatus = new HashMap<>();

        Map<String, Object> Metrics = (Map<String, Object>) status.get("Metrics");
        double CPU = (double) ((Map<String, Object>) Metrics.get("CPU Usage")).get("Percent");
        newStatus.put("CPU", CPU);

        Map<String, Object> Memory = (Map<String, Object>) Metrics.get("Memory Usage");
        int MemoryValue = (int) Math.round((double) Memory.get("RawValue"));
        int MemoryMax = (int) Math.round((double) Memory.get("MaxValue"));
        newStatus.put("MemoryValue", MemoryValue);
        newStatus.put("MemoryMax", MemoryMax);

        Map<String, Object> Players = (Map<String, Object>) Metrics.get("Active Users");
        int PlayersValue = (int) Math.round((double) Players.get("RawValue"));
        int PlayersMax = (int) Math.round((double) Players.get("MaxValue"));
        newStatus.put("PlayersValue", PlayersValue);
        newStatus.put("PlayersMax", PlayersMax);

        Map<String, Object> TPS = (Map<String, Object>) Metrics.get("TPS");
        double TPSValue = (double) TPS.get("RawValue");
        newStatus.put("TPSValue", TPSValue);

        // Convert state to string
        int state = (int) Math.round((double) status.get("State"));
        switch (state) {
            case -1:
                newStatus.put("State", "Undefined");
                break;
            case 0:
                newStatus.put("State", "Stopped");
                break;
            case 5:
                newStatus.put("State", "PreStart");
                break;
            case 7:
                newStatus.put("State", "Configuring");
                break;
            case 10:
                newStatus.put("State", "Starting");
                break;
            case 20:
                newStatus.put("State", "Ready");
                break;
            case 30:
                newStatus.put("State", "Restarting");
                break;
            case 40:
                newStatus.put("State", "Stopping");
                break;
            case 45:
                newStatus.put("State", "PreparingForSleep");
                break;
            case 50:
                newStatus.put("State", "Sleeping");
                break;
            case 60:
                newStatus.put("State", "Waiting");
                break;
            case 70:
                newStatus.put("State", "Installing");
                break;
            case 75:
                newStatus.put("State", "Updating");
                break;
            case 80:
                newStatus.put("State", "AwaitingUserInput");
                break;
            case 100:
                newStatus.put("State", "Failed");
                break;
            case 200:
                newStatus.put("State", "Suspended");
                break;
            case 250:
                newStatus.put("State", "Maintenance");
                break;
            case 999:
                newStatus.put("State", "Indeterminate");
                break;
        }

        return newStatus;
    }

    // Start Server Handler
    private String startServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp start <server>";
        }
        // Start server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Map<String, Object> status = getStatus(serverName);
                int state = (int) Math.round((double) status.get("State"));
                if (!Objects.equals(state, 10) && !Objects.equals(state, 20)) {
                    startServer(serverName);
                    return "§aStarting server " + serverName + "...";
                } else {
                    return "§cServer " + serverName + " is already running!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Stop Server Handler
    private String stopServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp stop <instance>";
        }
        // Stop server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Map<String, Object> status = getStatus(serverName);
                int state = (int) Math.round((double) status.get("State"));
                if (!Objects.equals(state, 0) && !Objects.equals(state, 40)) {
                    stopServer(serverName);
                    return "§aStopping server " + serverName + "...";
                } else {
                    return "§cServer " + serverName + " is already stopped!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Restart Server Handler
    private String restartServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp restart <instance>";
        }
        // Restart server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Map<String, Object> status = getStatus(serverName);
                int state = (int) Math.round((double) status.get("State"));
                if (!Objects.equals(state, 0) && !Objects.equals(state, 40) && !Objects.equals(state, 45) && !Objects.equals(state, 50) ) {
                    restartServer(serverName);
                    return "§aRestarting server " + serverName + "...";
                } else {
                    return "§cServer " + serverName + " is stopped/sleeping!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Kill Server Handler
    private String killServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp kill <instance>";
        }
        // Kill server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                killServer(serverName);
                return "§aKilling server " + serverName + "...";
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Sleep Server Handler
    private String sleepServerHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp sleep <instance>";
        }
        // Sleep server
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Map<String, Object> status = getStatus(serverName);
                int state = (int) Math.round((double) status.get("State"));
                if (!Objects.equals(state, 0) && !Objects.equals(state, 30) && !Objects.equals(state, 40)) {
                    sleepServer(serverName);
                    return "§aPutting server " + serverName + " to sleep...";
                } else {
                    return "§cServer " + serverName + " is already stopped/sleeping!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Send Command Handler
    private String sendCommandHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp command <instance> <command>";
        }
        // Send command
        if (args.length >= 3) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Map<String, Object> status = getStatus(serverName);
                int state = (int) Math.round((double) status.get("State"));
                if (Objects.equals(state, 20)) {
                    String command = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    sendCommand(serverName, command);
                    return "§aSending command to server " + serverName + "...";
                } else {
                    return "§cServer " + serverName + " is not Ready!";
                }
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Get Status Handler
    private String getStatusHandler(String[] args) {
        // Usage
        if (args.length == 1) {
            return "§cUsage: /amp status <instance>";
        }
        // Get status
        if (args.length == 2) {
            String serverName = args[1];
            if (instances.containsKey(serverName)) {
                Map<String, Object> status = parseStatus(serverName);
                if (status == null) {
                    return "§cServer " + serverName + " is not responding!";
                }
                return "§6" + serverName + ":" +
                    "\n§6Status: §5" + status.get("State") +
                    "\n§6CPU: §9" + status.get("CPU") + "§6%" +
                    "\n§6Memory: §9" + status.get("MemoryValue") + "§6/§9" + status.get("MemoryMax") + "§6MB" +
                    "\n§6Players: §9" + status.get("PlayersValue") + "§6/§9" + status.get("PlayersMax") +
                    "\n§6TPS: §2" + status.get("TPSValue");
            } else {
                return "§cServer " + serverName + " does not exist!";
            }
        }
        return null;
    }

    // Server Command
    private String serverCommand(String[] args, Function<String[], String> method) {
        return method.apply(args);
    }

    // Command Messenger
    public String commandMessenger(String[] args) {
        String message = "";
        try {
            switch (args[0].toLowerCase()) {
                // Start Server
                case "start":
                    message = serverCommand(args, this::startServerHandler);
                    break;
                // Stop Server
                case "stop":
                    message = serverCommand(args, this::stopServerHandler);
                    break;
                // Restart Server
                case "restart":
                    message = serverCommand(args, this::restartServerHandler);
                    break;
                // Kill Server
                case "kill":
                    message = serverCommand(args, this::killServerHandler);
                    break;
                // Sleep Server
                case "sleep":
                    message = serverCommand(args, this::sleepServerHandler);
                    break;
                // Send Command
                case "send":
                    message = serverCommand(args, this::sendCommandHandler);
                    break;
                // Get Status
                case "status":
                    message = serverCommand(args, this::getStatusHandler);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            AMPServerManager.Instance instance = instances.get(args[1]);
            boolean result = instanceLogin(instance);
            if (result) {
                message = "§cAn error occurred while executing the command!";
            } else {
                message = "§cAn error occurred while logging into the instance!";
            }
        }
        return message;
    }
}
