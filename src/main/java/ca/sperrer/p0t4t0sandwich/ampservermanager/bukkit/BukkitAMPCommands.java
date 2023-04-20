package ca.sperrer.p0t4t0sandwich.ampservermanager.bukkit;

import ca.sperrer.p0t4t0sandwich.ampservermanager.AMPServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitAMPCommands implements CommandExecutor {
    private final BukkitMain plugin = BukkitMain.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        AMPServerManager.runTaskAsync(() -> {
            String message;
            // Player and permission check
            if (sender instanceof Player && !sender.hasPermission("ampservermanager.amp")) {
                message = "§cYou do not have permission to use this command!";
            } else {
                message = args.length == 0 ? "§cUsage: /amp <command>" : plugin.ampServerManager.commandMessenger(args);
            }
            sender.sendMessage(message);
        });
        return true;
    }
}
