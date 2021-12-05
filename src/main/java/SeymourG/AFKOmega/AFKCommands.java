package SeymourG.AFKOmega;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

    private static int HandleAFKCommand(CommandSource commandSource) throws CommandSyntaxException {
        Entity entity = commandSource.assertIsEntity();
        ServerPlayerEntity player = AFKOmega.getServerPlayerEntity(entity);
        AFKOmega.manualAFK(player);
        return 1;
    }
}