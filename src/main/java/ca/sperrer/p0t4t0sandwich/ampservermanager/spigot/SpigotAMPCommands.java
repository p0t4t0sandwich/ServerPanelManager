package ca.sperrer.p0t4t0sandwich.ampservermanager.spigot;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpigotAMPCommands implements CommandExecutor {
    private final SpigotMain plugin = SpigotMain.getInstance();
    private final AMPServerManager ampServerManager = SpigotAMPServerManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String message;
                // Player and permission check
                if (sender instanceof Player && !sender.hasPermission("ampservermanager.amp")) {
                    message = "§cYou do not have permission to use this command!";
                } else {
                    message = args.length == 0 ? "§cUsage: /amp <command>" : ampServerManager.commandMessenger(args);
                }
                sender.sendMessage(message);
            }
        }.runTask(plugin);
        return true;
    }
}
