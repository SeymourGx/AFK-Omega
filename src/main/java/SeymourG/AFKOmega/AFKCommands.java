package SeymourG.AFKOmega;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class AFKCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
            Commands.literal("AFK")
            .executes((commandSource) -> HandleAFKCommand(commandSource.getSource()))
        );
    }

    private static int HandleAFKCommand(CommandSource commandSource) {
        try {
            Entity entity = commandSource.getEntity();
            ServerPlayerEntity player = AFKOmega.getServerPlayerEntity(entity);
            AFKOmega.manualAFK(player);
            return 1;
        }
        catch (Exception e) {
            return -1;
        }
    }
}