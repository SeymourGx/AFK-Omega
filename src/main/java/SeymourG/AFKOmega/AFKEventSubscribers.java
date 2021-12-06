package SeymourG.AFKOmega;

import java.util.Date;
import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

public class AFKEventSubscribers {
    private static final int TICKS_PER_SECOND = 20;
    private static int TICK_COUNTER = 0;

    @SubscribeEvent
    public static void handleKeybindInput(TickEvent.ClientTickEvent event) {
        // Runs every time the Toggle AFK keybind is pressed
        if (AFKOmega.modKeyBinding.isPressed()) {
            Entity entity = Minecraft.getInstance().player.getEntity();
            ServerPlayerEntity player = AFKOmega.getServerPlayerEntity(entity);
            AFKOmega.manualAFK(player);
        }
    }

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        // Only ever runs once, on server startup
        AFKOmega.LOG("AFK Omega has begun.", 0);
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        // Only ever runs once, on server shutdown
        AFKOmega.LOG("AFK Omega is no more. Goodbye!", 0);
    }

    @SubscribeEvent
    public static void onServerTickEvent(TickEvent.ServerTickEvent tick) {
        // All event handlers that NEED to fire on EVERY Server tick
        if (tick.phase == TickEvent.Phase.END) {
            // intentionally blank
            if (++TICK_COUNTER >= TICKS_PER_SECOND) {
                // All event handlers that can survive on only firing every second or so
                TICK_COUNTER = 0;
                AFKEventHandlers.afkCooldown();
                AFKEventHandlers.automaticAFK();
            }
        }
    }

    @SubscribeEvent
    public static void onInputEvent(InputEvent event) {
        // All event handlers that NEED to fire on EVERY Player input in-game
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            AFKEventHandlers.logInput();
        }
    }

    @SubscribeEvent
    public static void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        // All event handlers that NEED to fire on EVERY keyboard input in-game
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            AFKEventHandlers.checkIfPlayerMovedWhileAFK(player.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractEvent(PlayerInteractEvent event) {
        // All event handlers that NEED to fire on EVERY player interaction in-game
        ServerPlayerEntity player = AFKOmega.getServerPlayerEntity(event.getEntity());
        AFKEventHandlers.checkIfPlayerMovedWhileAFK(player.getEntity());
    }

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        // Only ever runs once, on modload
        CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();
        AFKCommands.register(commandDispatcher);
    }

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        // Only runs whenever a player logs in
        // Sets the first player input as their login time
        Entity entity = event.getEntity();
        UUID uuid = entity.getUniqueID();
        AFKOmega.updatePlayerInput(uuid, new Date());
        AFKOmega.updatePlayerLocation(uuid, AFKOmega.getCurrentPlayerLocation(entity));
    }

    @SubscribeEvent
    public static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        // Only runs whenever a player logs out
        // Clears the player's data from all maps/lists as a best practice for larger or long-running servers
        AFKOmega.purgePlayer(event.getEntity().getUniqueID());
    }
}