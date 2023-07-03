package dev.neuralnexus.serverpanelmanager.common.commands;

import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.Utils;
import dev.neuralnexus.serverpanelmanager.common.player.AbstractPlayer;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;

public interface SPMCommand {
    static String getCommandName() {
        return "spm";
    }

    static String getCommandDescription() {
        return "Root command for Server Panel Manager.";
    }

    static String permissionBuilder(String[] args) {
        if (args.length == 0) {
            return "spm.command";
        } else if (args.length == 1) {
            return "spm.command." + args[0].toLowerCase();
        } else if (args.length == 2) {
            return "spm.command." + args[0].toLowerCase() + "." + args[1].toLowerCase();
        } else {
            return "spm.command." + args[0].toLowerCase() + "." + args[1].toLowerCase() + "." + args[2].toLowerCase();
        }
    }

    static String executeCommand(String[] args) {
        return args.length == 0 ? "§cUsage: /spm <command>" : ServerPanelManager.commandHandler.commandMessenger(args);
    }

    static void executeCommand(AbstractPlayer player, boolean isPlayer, String[] args) {
        if (isPlayer) {
            if (!player.hasPermission(permissionBuilder(args))) {
                player.sendMessage("§cYou do not have permission to use this command.");
            } else {
                player.sendMessage(executeCommand(args));
            }
        } else {
            ServerPanelManager.useLogger(ansiiParser(executeCommand(args)));
        }
    }
}
