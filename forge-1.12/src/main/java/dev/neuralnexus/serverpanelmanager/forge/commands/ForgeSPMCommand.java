package dev.neuralnexus.serverpanelmanager.forge.commands;

import dev.neuralnexus.serverpanelmanager.common.commands.SPMCommand;
import dev.neuralnexus.serverpanelmanager.forge.player.ForgePlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import static dev.neuralnexus.serverpanelmanager.common.Utils.runTaskAsync;

/**
 * Forge implementation of the SPM command.
 */
@Mod.EventBusSubscriber(modid = "serverpanelmanager")
public class ForgeSPMCommand extends CommandBase {
    /**
     * Registers the command.
     * @param event The command registration event.
     */
    @EventHandler
    public static void registerCommand(FMLServerStartingEvent event) {
//        int permissionLevel;
//        String commandName;
//        if (Loader.getDist().isDedicatedServer()) {
//            // Check if LuckPerms is hooked
//            permissionLevel = LuckPermsHook.isHooked() ? 0 : 4;
//            commandName = "spm";
//        } else {
//            permissionLevel = 0;
//            commandName = "spmc";
//        }

        event.registerServerCommand(new ForgeSPMCommand());
    }

    @Override
    public String getName() {
        return "spm";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "§cUsage: /spm <command>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        runTaskAsync(() -> {
            try {
                // Check if sender is a player
                boolean isPlayer = sender instanceof EntityPlayer;
                ForgePlayer player = isPlayer ? new ForgePlayer((EntityPlayer) sender) : null;

                // Execute command
                SPMCommand.executeCommand(player, isPlayer, args);
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
            }
        });
    }
}
