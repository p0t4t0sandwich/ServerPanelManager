package dev.neuralnexus.serverpanelmanager.forge.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.neuralnexus.serverpanelmanager.common.commands.SPMCommand;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import dev.neuralnexus.serverpanelmanager.forge.player.ForgePlayer;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

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
        if (event.getEnvironment() == Commands.EnvironmentType.DEDICATED) {
            // Check if LuckPerms is hooked
            permissionLevel = LuckPermsHook.isHooked() ? 0 : 4;
            commandName = "spm";
        } else {
            permissionLevel = 0;
            commandName = "spmc";
        }

        event.getDispatcher().register(literal(commandName)
            .requires(source -> source.hasPermissionLevel(permissionLevel))
            .then(argument("command", StringArgumentType.greedyString())
                .executes(context -> {
                    runTaskAsync(() -> {
                        try {
                            String[] args = context.getArgument("command", String.class).split(" ");

                            // Check if sender is a player
                            boolean isPlayer = context.getSource().getEntity() instanceof PlayerEntity;
                            ForgePlayer player = isPlayer ? new ForgePlayer((PlayerEntity) context.getSource().getEntity()) : null;

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
