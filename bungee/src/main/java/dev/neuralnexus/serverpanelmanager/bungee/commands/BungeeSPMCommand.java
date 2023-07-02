package dev.neuralnexus.serverpanelmanager.bungee.commands;

import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

public class BungeeSPMCommand extends Command {
    public BungeeSPMCommand() {
        super("spmb");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        runTaskAsync(() -> {
            try {
                // Check if sender is a player
                if ((sender instanceof ProxiedPlayer)) {
                    ProxiedPlayer player = (ProxiedPlayer) sender;

                    // Permission check
                    if (!player.hasPermission("template.command")) {
                        player.sendMessage(new ComponentBuilder("§cYou do not have permission to use this command.").create());
                        return;
                    }

                    String text = args.length == 0 ? "§cUsage: /spmb <command>" : ServerPanelManager.commandHandler.commandMessenger(args);
                    player.sendMessage(new ComponentBuilder(text).create());
                } else {
                    String text = args.length == 0 ? "§cUsage: /spmb <command>" : ServerPanelManager.commandHandler.commandMessenger(args);
                    ServerPanelManager.useLogger(ansiiParser(text));
                }
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
