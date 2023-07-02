package dev.neuralnexus.serverpanelmanager.forge.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
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
        // Check if LuckPerms is hooked
//        int permissionLevel = LuckPermsHook.isHooked() ? 0 : 4;

//        String commandName = environment.name().equals("DEDICATED") ? "spm" : "spmc";
//        int permissionLevel = environment.name().equals("DEDICATED") ? 4 : 0;

        event.getDispatcher().register(literal("spm")
            .requires(source -> source.hasPermission(4))
            .then(argument("command", StringArgumentType.greedyString())
                .executes(context -> {
                    runTaskAsync(() -> {
                        try {
                            String[] args = new String[] {context.getArgument("command", String.class)};

                            // Send message to player or console
                            Entity entity = context.getSource().getEntity();
                            if (entity instanceof ServerPlayer) {
                                String text = ServerPanelManager.commandHandler.commandMessenger(args);
                                ((ServerPlayer) entity).displayClientMessage(Component.empty().append(text), false);
                            } else {
                                String text = ServerPanelManager.commandHandler.commandMessenger(args);
                                ServerPanelManager.useLogger(ansiiParser(text));
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
