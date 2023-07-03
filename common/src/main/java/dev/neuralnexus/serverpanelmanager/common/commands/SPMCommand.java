package dev.neuralnexus.serverpanelmanager.common.commands;

import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;

public interface SPMCommand {
    static String getCommandName() {
        return "spm";
    }

    static String getCommandDescription() {
        return "Root command for Server Panel Manager.";
    }

    static String getCommandPermission() {
        return "spm.command";
    }

    static String executeCommand(String[] args) {
        return args.length == 0 ? "§cUsage: /spmb <command>" : ServerPanelManager.commandHandler.commandMessenger(args);
    }
}
