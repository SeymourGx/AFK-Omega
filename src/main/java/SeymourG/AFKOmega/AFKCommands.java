package SeymourG.AFKOmega;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;

public class AFKCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
            Commands.literal("AFK")
            .executes((commandSource) -> HandleAFKCommand(commandSource.getSource()))
        );
    }

    private static int HandleAFKCommand(CommandSource commandSource) throws CommandSyntaxException {
        Entity entity = commandSource.assertIsEntity();
        ServerPlayerEntity player = AFKOmega.getServerPlayerEntity(entity);
        ToggleAFK(player);
        return 1;
    }

    public static void ToggleAFK(ServerPlayerEntity player) {
        // Immediately exiting method if the input delay cooldown is still in effect for this player
        if (AFKOmega.checkAFKDelay(player.getUniqueID())) {
            return;
        }
        List<ServerPlayerEntity> allPlayersList = AFKOmega.getAllPlayers();
        String username = player.getDisplayName().toString();

        for(ServerPlayerEntity tempPlayer : allPlayersList) {
            if (tempPlayer == player) {
                if (AFKOmega.getAFKPlayers().contains(tempPlayer.getUniqueID())) {
                    tempPlayer.sendMessage(new StringTextComponent("You are no longer AFK."), tempPlayer.getUniqueID());
                    AFKOmega.removeAFKPlayer(tempPlayer.getUniqueID());
                }
                else {
                    tempPlayer.sendMessage(new StringTextComponent("You have gone AFK."), tempPlayer.getUniqueID());
                    AFKOmega.addAFKPlayer(tempPlayer.getUniqueID());
                }
            } else {
                if (AFKOmega.getAFKPlayers().contains(player.getUniqueID())) {
                    tempPlayer.sendMessage(new StringTextComponent(String.format("%s is no longer AFK.", username)), tempPlayer.getUniqueID());
                }
                else {
                    tempPlayer.sendMessage(new StringTextComponent(String.format("%s is now AFK.", username)), tempPlayer.getUniqueID());
                }
            }
        }
    }
}