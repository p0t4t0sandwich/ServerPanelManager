package dev.neuralnexus.serverpanelmanager.bukkit.commands;

import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

public class BukkitSPMCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        AtomicBoolean success = new AtomicBoolean(false);
        runTaskAsync(() -> {
            try {
                // Check if sender is a player
                if ((sender instanceof Player)) {
                    Player player = (Player) sender;

                    // Permission check
                    if (!player.hasPermission("spm.command")) {
                        player.sendMessage("§cYou do not have permission to use this command.");
                        return;
                    }

                    String text = args.length == 0 ? "§cUsage: /spm <command>" : ServerPanelManager.commandHandler.commandMessenger(args);
                    player.sendMessage(text);
                } else {
                    String text = args.length == 0 ? "§cUsage: /spm <command>" : ServerPanelManager.commandHandler.commandMessenger(args);
                    ServerPanelManager.useLogger(ansiiParser(text));
                }
                success.set(true);
            } catch (Exception e) {
                success.set(false);
                System.err.println(e);
                e.printStackTrace();
            }
        });
        return success.get();
    }
}
