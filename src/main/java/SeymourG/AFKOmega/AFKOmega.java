package SeymourG.AFKOmega;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
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

// The value here should match an entry in the META-INF/mods.toml file
@Mod("afkomega")
public class AFKOmega
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MODID = "afkomega";
    public static final int AFKDELAY = 30; // AFK Delay in ticks
    public static KeyBinding modKeyBinding;
    private static Map<UUID, Integer> AFKDelays;

    public static Map<UUID, Integer> getAFKDelays() {
        return AFKDelays;
    }

    public static void removeAFKDelay(UUID uuid) {
        AFKDelays.remove(uuid);
    }

    public static boolean checkAFKDelay(UUID uuid) {
        return AFKDelays.containsKey(uuid);
    }

    private static List<UUID> AFKPlayers;

    public static List<UUID> getAFKPlayers() {
        return AFKPlayers;
    }

    public static void addAFKPlayer(UUID PlayerID) {
        AFKPlayers.add(PlayerID);
        AFKDelays.put(PlayerID, AFKDELAY);
    }

    public static void removeAFKPlayer(UUID PlayerID) {
        AFKPlayers.remove(PlayerID);
        AFKDelays.put(PlayerID, AFKDELAY);
    }
    
    public static List<ServerPlayerEntity> getAllPlayers() {
        return Minecraft.getInstance().getIntegratedServer().getPlayerList().getPlayers();
    }
    
    public static ServerPlayerEntity getServerPlayerEntity(Entity entity) {
        List<ServerPlayerEntity> allPlayersList = getAllPlayers();
        ServerPlayerEntity player = null;

        for(ServerPlayerEntity tempPlayer : allPlayersList) {
            if (tempPlayer.getUniqueID().equals(entity.getUniqueID())) {
                player = tempPlayer;
                break;
            }
        }
        
        return player;
    }

    public AFKOmega() {
        // Register these methods for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(RegisterCommandEvent.class);
        MinecraftForge.EVENT_BUS.register(RegisterInputEvent.class);
        MinecraftForge.EVENT_BUS.register(AFKDelay.class);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("AFK Omega has being its preinit setup.");
        AFKPlayers = new ArrayList<>();
        AFKDelays = new HashMap<>();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("AFK Omega has acquired the game settings {}", event.getMinecraftSupplier().get().gameSettings);

        // Handle Key Binding(s)
        modKeyBinding = new KeyBinding("Go AFK", 93, "AFK Omega");
        ClientRegistry.registerKeyBinding(modKeyBinding);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        //InterModComms.sendTo("AFKOmega", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Receive and process InterModComms
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("AFK Omega has begun.");
    }
}