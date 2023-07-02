package dev.neuralnexus.serverpanelmanager.fabric.mixin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.neuralnexus.serverpanelmanager.common.ServerPanelManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.neuralnexus.serverpanelmanager.common.Utils.ansiiParser;
import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Mixin(CommandManager.class)
public class FabricSPMCommand {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;setConsumer(Lcom/mojang/brigadier/ResultConsumer;)V"))
    private void registerTaterAPICommand(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci) {
        // Check if LuckPerms is hooked
//        int permissionLevel = LuckPermsHook.isHooked() ? 0 : 4;

        String commandName = environment.name().equals("DEDICATED") ? "spm" : "spmc";
        int permissionLevel = environment.name().equals("DEDICATED") ? 4 : 0;

        // Register command
        this.dispatcher.register(literal(commandName)
                .requires(source -> source.hasPermissionLevel(permissionLevel))
                .then(argument("command", StringArgumentType.greedyString())
                        .executes(context -> {
                            runTaskAsync(() -> {
                                try {
                                    String[] args = new String[] {context.getArgument("command", String.class)};

                                    // Send message to player or console
                                    Entity entity = context.getSource().getEntity();
                                    if (entity instanceof ServerPlayerEntity) {
                                        String text = ServerPanelManager.commandHandler.commandMessenger(args);
                                        entity.sendMessage(Text.of(text));
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
