package dev.neuralnexus.serverpanelmanager.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.neuralnexus.serverpanelmanager.common.commands.SPMCommand;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import dev.neuralnexus.serverpanelmanager.fabric.player.FabricPlayer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Fabric implementation of the SPM command.
 */
public class FabricSPMCommand {
    /**
     * Registers the command.
     * @param dispatcher The command dispatcher.
     * @param registryAccess The command registry access.
     * @param environment The command registration environment.
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        int permissionLevel;
        String commandName;
        if (environment.name().equals("DEDICATED")) {
            // Check if LuckPerms is hooked
            permissionLevel = LuckPermsHook.isHooked() ? 0 : 4;
            commandName = "spm";
        } else {
            permissionLevel = 0;
            commandName = "spmc";
        }

        // Register command
        dispatcher.register(literal(commandName)
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                .then(argument("command", StringArgumentType.greedyString())
                        .executes(context -> {
                            runTaskAsync(() -> {
                                try {
                                    String[] args = context.getArgument("command", String.class).split(" ");

                                    // Check if sender is a player
                                    boolean isPlayer = context.getSource().getEntity() instanceof ServerPlayerEntity;
                                    FabricPlayer player = isPlayer ? new FabricPlayer((ServerPlayerEntity) context.getSource().getEntity()) : null;

                                    // Execute command
                                    SPMCommand.executeCommand(player, isPlayer, args);
                                } catch (Exception e) {
                                    System.err.println(e);
                                    e.printStackTrace();
                                }
                            });
                            return 1;
                        })
                ));
    }
}
