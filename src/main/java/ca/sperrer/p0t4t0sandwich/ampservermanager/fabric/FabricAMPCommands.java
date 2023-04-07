package ca.sperrer.p0t4t0sandwich.ampservermanager.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.concurrent.*;

public final class FabricAMPCommands {
    private static final FabricMain mod = FabricMain.getInstance();

    // TODO: Consolidate this with Forge, input the environment as a parameter
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        String commandName = environment.dedicated ? "amp" : "camp";
        int permissionLevel = environment.dedicated ? 4 : 0;
        dispatcher.register(literal(commandName)
            .requires(source -> source.hasPermissionLevel(permissionLevel))
            .then(argument("command", StringArgumentType.greedyString())
            .executes(context -> {
                try {
                    (new Thread(() -> {
                        // Get arguments and send to commandMessenger
                        String[] args = context.getInput().split(" ");
                        args = Arrays.copyOfRange(args, 1, args.length);
                        String message = args.length == 0 ? "§cUsage: /amp <command>" : mod.ampServerManager.commandMessenger(args);

                        // Check if message is null or empty
                        if (message == null || message.equals("")) {
                            message = "§cAn error occurred while executing the command!";
                        }

                        // Send message to player or console
                        Entity entity = context.getSource().getEntity();
                        if (entity instanceof ServerPlayerEntity) {
                            entity.sendMessage(Text.literal(message));
                        } else {
                            mod.logger.info(message.replaceAll("§.", ""));
                        }
                    })).start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return 1;
            })
        ));
    }
}
