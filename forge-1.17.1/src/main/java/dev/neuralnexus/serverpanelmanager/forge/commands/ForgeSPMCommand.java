package dev.neuralnexus.serverpanelmanager.forge.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.neuralnexus.serverpanelmanager.common.commands.SPMCommand;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import dev.neuralnexus.serverpanelmanager.forge.player.ForgePlayer;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Forge implementation of the SPM command.
 */
@Mod.EventBusSubscriber(modid = "serverpanelmanager")
public class ForgeSPMCommand {
    /**
     * Registers the command.
     * @param event The command registration event.
     */
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        int permissionLevel;
        String commandName;
        if (event.getEnvironment() == Commands.CommandSelection.DEDICATED) {
            // Check if LuckPerms is hooked
            permissionLevel = LuckPermsHook.isHooked() ? 0 : 4;
            commandName = "spm";
        } else {
            permissionLevel = 0;
            commandName = "spmc";
        }

        event.getDispatcher().register(literal(commandName)
            .requires(source -> source.hasPermission(permissionLevel))
            .then(argument("command", StringArgumentType.greedyString())
                .executes(context -> {
                    runTaskAsync(() -> {
                        try {
                            String[] args = new String[] {context.getArgument("command", String.class)};

                            // Check if sender is a player
                            boolean isPlayer = context.getSource().getEntity() instanceof ServerPlayer;
                            ForgePlayer player = isPlayer ? new ForgePlayer((ServerPlayer) context.getSource().getEntity()) : null;

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
