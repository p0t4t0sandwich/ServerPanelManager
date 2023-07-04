package dev.neuralnexus.serverpanelmanager.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import dev.neuralnexus.serverpanelmanager.common.commands.SPMCommand;
import dev.neuralnexus.serverpanelmanager.velocity.player.VelocityPlayer;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Velocity implementation of the SPM command.
 */
public class VelocitySPMCommand implements SimpleCommand {
    /**
     * @inheritDoc
     */
    @Override
    public void execute(Invocation invocation) {
//        runTaskAsync(() -> {
            try {
                String[] args = invocation.arguments();

                // Check if sender is a player
                boolean isPlayer = invocation.source() instanceof Player;
                VelocityPlayer player = isPlayer ? new VelocityPlayer((Player) invocation.source()) : null;

                // Execute command
                SPMCommand.executeCommand(player, isPlayer, args);
            } catch (Exception e) {
                System.out.println(e);
                e.printStackTrace();
            }
//        });
    }
}
