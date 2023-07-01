package dev.neuralnexus.serverpanelmanager.forge;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ForgeCommandEvent {
    private static final ForgeMain mod = ForgeMain.getInstance();

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        // Register command
        String commandName = "psm";
        int permissionLevel = 4;
        event.getDispatcher().register(
                literal(commandName)
                .requires(source -> source.hasPermission(permissionLevel))
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
                                    if (entity instanceof ServerPlayer) {
                                        ((ServerPlayer) entity).displayClientMessage(Component.empty().append(message), false);
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
