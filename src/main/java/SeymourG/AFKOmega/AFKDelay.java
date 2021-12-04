package SeymourG.AFKOmega;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AFKDelay {
    private static final int AFKDELAY = AFKOmega.AFKDELAY;

    @SubscribeEvent
    public static void afkCooldown(TickEvent.WorldTickEvent tick) {
        Map<UUID, Integer> AFKDelays = AFKOmega.getAFKDelays();

        for (Map.Entry<UUID, Integer> entry : AFKDelays.entrySet()) {
            int newValue = entry.getValue() - 1;
            if (newValue > 0) {
                entry.setValue(newValue);
            }
            else {
                AFKOmega.removeAFKDelay(entry.getKey());
            }
        }
    }
}