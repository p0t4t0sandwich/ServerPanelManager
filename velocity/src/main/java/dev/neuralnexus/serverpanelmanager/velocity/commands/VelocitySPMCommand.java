package dev.neuralnexus.serverpanelmanager.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.commands.SPMCommand;
import net.kyori.adventure.text.Component;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

public class VelocitySPMCommand implements SimpleCommand, SPMCommand {
    @Override
    public void execute(Invocation invocation) {
//        runTaskAsync(() -> {
            try {
                String[] args = invocation.arguments();

                // Check if sender is a player
                if ((invocation.source() instanceof Player)) {
                    Player player = (Player) invocation.source();

                    // Permission check
                    if (!player.hasPermission(SPMCommand.getCommandPermission())) {
                        player.sendMessage(Component.text("§cYou do not have permission to use this command."));
                        return;
                    }
                    player.sendMessage(Component.text(SPMCommand.executeCommand(args)));
                } else {
                    ServerPanelManager.useLogger(ansiiParser(SPMCommand.executeCommand(args)));
                }
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
//        });
    }
}
