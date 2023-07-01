package dev.neuralnexus.serverpanelmanager.bukkit.commands;

import dev.neuralnexus.serverpanelmanager.common.Template;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

public class BukkitTemplateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        AtomicBoolean success = new AtomicBoolean(false);
        runTaskAsync(() -> {
            try {
                // Check if sender is a player
                if ((sender instanceof Player)) {
                    Player player = (Player) sender;

                    // Permission check
                    if (!player.hasPermission("template.command")) {
                        player.sendMessage("§cYou do not have permission to use this command.");
                        return;
                    }

                    String text = "";

                    player.sendMessage(text);
                } else {
                    Template.useLogger(ansiiParser("§cYou must be a player to use this command."));
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
