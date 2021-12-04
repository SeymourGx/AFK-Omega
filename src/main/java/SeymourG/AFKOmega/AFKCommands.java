package SeymourG.AFKOmega;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
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
        List<ServerPlayerEntity> allPlayersList = commandSource.getServer().getPlayerList().getPlayers();
        ServerPlayerEntity endPlayer = null;

        for(ServerPlayerEntity tempPlayer : allPlayersList) {
            if (tempPlayer.getUniqueID().equals(entity.getUniqueID())) {
                endPlayer = tempPlayer;
            }
        }
        ToggleAFK(endPlayer);
        return 1;
    }

    public static void ToggleAFK(ServerPlayerEntity player) {
        List<ServerPlayerEntity> allPlayersList = Objects.requireNonNull(Minecraft.getInstance().getIntegratedServer()).getPlayerList().getPlayers();
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