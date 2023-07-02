package dev.neuralnexus.serverpanelmanager.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import net.kyori.adventure.text.Component;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

public class VelocitySPMCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
//        runTaskAsync(() -> {
            try {
                String[] args = invocation.arguments();

                // Check if sender is a player
                if ((invocation.source() instanceof Player)) {
                    Player player = (Player) invocation.source();

                    // Permission check
                    if (!player.hasPermission("spm.command")) {
                        player.sendMessage(Component.text("§cYou do not have permission to use this command."));
                        return;
                    }

                    String text = args.length == 0 ? "§cUsage: /spm <command>" : ServerPanelManager.commandHandler.commandMessenger(args);;
                    player.sendMessage(Component.text(text));
                } else {
                    String text = args.length == 0 ? "§cUsage: /spm <command>" : ServerPanelManager.commandHandler.commandMessenger(args);;
                    ServerPanelManager.useLogger(ansiiParser(text));
                }
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
//        });
    }
}
