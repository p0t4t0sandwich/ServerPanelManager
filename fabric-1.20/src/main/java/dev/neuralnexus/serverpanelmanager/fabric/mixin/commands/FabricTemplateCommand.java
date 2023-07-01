package dev.neuralnexus.serverpanelmanager.fabric.mixin.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.neuralnexus.template.common.Template;
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

import static dev.neuralnexus.template.common.Utils.ansiiParser;
import static dev.neuralnexus.template.common.Utils.runTaskAsync;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

@Mixin(CommandManager.class)
public class FabricTemplateCommand {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"))
    private void registerTaterAPICommand(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
        // Check if LuckPerms is hooked
//        int permissionLevel = LuckPermsHook.isHooked() ? 0 : 4;

        // Register command
        this.dispatcher.register(literal("pronouns")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("pronouns", StringArgumentType.greedyString())
                        .executes(context -> {
                            runTaskAsync(() -> {
                                try {
                                    String[] args = new String[] {context.getArgument("pronouns", String.class)};

                                    // Send message to player or console
                                    Entity entity = context.getSource().getEntity();
                                    if (entity instanceof ServerPlayerEntity) {
                                        String text = "";
                                        entity.sendMessage(Text.of(text));
                                    } else {
                                        Template.useLogger(ansiiParser("§cYou must be a player to use this command."));
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
