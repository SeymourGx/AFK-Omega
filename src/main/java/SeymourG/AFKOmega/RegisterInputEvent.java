package SeymourG.AFKOmega;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegisterInputEvent {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (AFKOmega.modKeyBinding.isPressed()) {
            Entity entity = Minecraft.getInstance().player.getEntity();
            ServerPlayerEntity player = AFKOmega.getServerPlayerEntity(entity);
            AFKCommands.ToggleAFK(player);
        }
    }
}
