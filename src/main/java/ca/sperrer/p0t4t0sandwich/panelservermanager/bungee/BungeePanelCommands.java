package ca.sperrer.p0t4t0sandwich.panelservermanager.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static ca.sperrer.p0t4t0sandwich.panelservermanager.Utils.runTaskAsync;

public class BungeePanelCommands extends Command {
    private final BungeeMain plugin = BungeeMain.getInstance();

    public BungeePanelCommands() {
        super("psmb");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        runTaskAsync(() -> {
            String message;
            // Player and permission check
            if (sender instanceof ProxiedPlayer && !sender.hasPermission("psm")) {
                message = "§cYou do not have permission to use this command!";
            } else {
                message = args.length == 0 ? "§cUsage: /psmb <command>" : plugin.panelServerManager.commandMessenger(args);
            }

            sender.sendMessage(new ComponentBuilder(message).create());

        });
    }
}
