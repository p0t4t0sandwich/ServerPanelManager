package dev.neuralnexus.serverpanelmanager.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.concurrent.*;

public final class FabricPanelCommands {
    private static final FabricMain mod = FabricMain.getInstance();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        String commandName = environment.dedicated ? "psm" : "psmc";
        int permissionLevel = environment.dedicated ? 4 : 0;
        dispatcher.register(literal(commandName)
            .requires(source -> source.hasPermissionLevel(permissionLevel))
            .then(argument("command", StringArgumentType.greedyString())
            .executes(context -> {
                try {
                    ForkJoinPool.commonPool().submit(() -> {
                        // Get arguments and send to commandMessenger
                        String[] args = context.getInput().split(" ");
                        args = Arrays.copyOfRange(args, 1, args.length);
                        String message = args.length == 0 ? "§cUsage: /psm <command>" : mod.panelServerManager.commandHandler.commandMessenger(args);

                        // Check if message is null or empty
                        if (message == null || message.equals("")) {
                            message = "§cAn error occurred while executing the command!";
                        }

                        // Send message to player or console
                        Entity entity = context.getSource().getEntity();
                        if (entity instanceof ServerPlayerEntity) {
                            entity.sendMessage(Text.literal(message));
                        } else {
                            mod.logger.info(ansiiParser(message));
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return 1;
            })
        ));
    }
}