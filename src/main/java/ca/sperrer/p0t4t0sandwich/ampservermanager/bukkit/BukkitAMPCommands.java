package ca.sperrer.p0t4t0sandwich.ampservermanager.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitAMPCommands implements CommandExecutor {
    private final BukkitMain plugin = BukkitMain.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        new BukkitRunnable() {
//            @Override
//            public void run() {
        (new Thread(() -> {
            String message;
            // Player and permission check
            if (sender instanceof Player && !sender.hasPermission("ampservermanager.amp")) {
                message = "§cYou do not have permission to use this command!";
            } else {
                message = args.length == 0 ? "§cUsage: /amp <command>" : plugin.ampServerManager.commandMessenger(args);
            }
            sender.sendMessage(message);
        })).start();
//            }
//        }.runTask(this);
        return true;
    }
}
