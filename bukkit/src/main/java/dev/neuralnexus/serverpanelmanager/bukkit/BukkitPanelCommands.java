package dev.neuralnexus.serverpanelmanager.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

public class BukkitPanelCommands implements CommandExecutor {
    private final BukkitMain plugin = BukkitMain.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        runTaskAsync(() -> {
            String message;
            // Player and permission check
            if (sender instanceof Player && !sender.hasPermission("psm")) {
                message = "§cYou do not have permission to use this command!";
            } else {
                message = args.length == 0 ? "§cUsage: /psm <command>" : plugin.panelServerManager.commandHandler.commandMessenger(args);
            }
            sender.sendMessage(message);
        });
        return true;
    }
}
