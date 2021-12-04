package SeymourG.AFKOmega;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegisterInputEvent {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (AFKOmega.modKeyBinding.isPressed()) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            ServerPlayerEntity endPlayer = null;
            List<ServerPlayerEntity> allPlayersList = Minecraft.getInstance().getIntegratedServer().getPlayerList().getPlayers();
            for (ServerPlayerEntity tempPlayer : allPlayersList) {
                if (tempPlayer.getUniqueID().equals(player.getUniqueID())) {
                    endPlayer = tempPlayer;
                }
            }
            AFKCommands.ToggleAFK(endPlayer);
        }
    }
}
