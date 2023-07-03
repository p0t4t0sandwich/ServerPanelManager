package dev.neuralnexus.serverpanelmanager.forge.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import dev.neuralnexus.serverpanelmanager.common.commands.SPMCommand;
import dev.neuralnexus.serverpanelmanager.common.hooks.LuckPermsHook;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public final class ForgeSPMCommand {
    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
//        int permissionLevel;
//        String commandName;
//        if (event.getEnvironment() == RegisterCommandsEvent.EnvironmentType.DEDICATED) {
//            // Check if LuckPerms is hooked
//            permissionLevel = LuckPermsHook.isHooked() ? 0 : 4;
//            commandName = "spm";
//        } else {
//            permissionLevel = 0;
//            commandName = "spmc";
//        }

        int permissionLevel = LuckPermsHook.isHooked() ? 0 : 4;
        String commandName = "spm";

        event.getDispatcher().register(literal(commandName)
            .requires(source -> source.hasPermission(permissionLevel))
            .then(argument("command", StringArgumentType.greedyString())
                .executes(context -> {
                    runTaskAsync(() -> {
                        try {
                            String[] args = new String[] {context.getArgument("command", String.class)};

                            // Send message to player or console
                            Entity entity = context.getSource().getEntity();
                            if (entity instanceof ServerPlayer) {
                                ((ServerPlayer) entity).displayClientMessage(Component.empty().append(SPMCommand.executeCommand(args)), false);
                            } else {
                                ServerPanelManager.useLogger((ansiiParser(SPMCommand.executeCommand(args))));
                            }
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
