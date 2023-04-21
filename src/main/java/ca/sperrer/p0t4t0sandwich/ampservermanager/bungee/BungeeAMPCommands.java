package ca.sperrer.p0t4t0sandwich.ampservermanager.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import static ca.sperrer.p0t4t0sandwich.ampservermanager.Utils.runTaskAsync;

public class BungeeAMPCommands extends Command {
    private final BungeeMain plugin = BungeeMain.getInstance();

    public BungeeAMPCommands() {
        super("bamp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        runTaskAsync(() -> {
            String message;
            // Player and permission check
            if (sender instanceof ProxiedPlayer && !sender.hasPermission("ampservermanager.amp")) {
                message = "§cYou do not have permission to use this command!";
            } else {
                message = args.length == 0 ? "§cUsage: /bamp <command>" : plugin.ampServerManager.commandMessenger(args);
            }

            sender.sendMessage(new ComponentBuilder(message).create());

        });
    }
}
