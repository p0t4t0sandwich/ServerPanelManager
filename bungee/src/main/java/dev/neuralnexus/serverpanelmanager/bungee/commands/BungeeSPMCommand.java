package dev.neuralnexus.serverpanelmanager.bungee.commands;

import dev.neuralnexus.serverpanelmanager.bungee.player.BungeePlayer;
import dev.neuralnexus.serverpanelmanager.common.commands.SPMCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Bungee implementation of the SPM command.
 */
public class BungeeSPMCommand extends Command {
    public BungeeSPMCommand() {
        super("spmb");
    }

    /**
     * @inheritDoc
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        runTaskAsync(() -> {
            try {
                // Check if sender is a player
                boolean isPlayer = sender instanceof ProxiedPlayer;
                BungeePlayer player = isPlayer ? new BungeePlayer((ProxiedPlayer) sender) : null;

                // Execute command
                SPMCommand.executeCommand(player, isPlayer, args);
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
