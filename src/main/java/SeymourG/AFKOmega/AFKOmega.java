package SeymourG.AFKOmega;

import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.userdev.LaunchTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("afkomega")
public class AFKOmega
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MODID = "afkomega";
    public static KeyBinding modKeyBinding;
    public static List<UUID> AFKPlayers;

    public static List<UUID> getAFKPlayers() {
        return AFKPlayers;
    }

    public static void setAFKPlayers(List<UUID> AFKPlayers) {
        AFKOmega.AFKPlayers = AFKPlayers;
    }

    public static void addAFKPlayer(UUID PlayerID) {
        AFKPlayers.add(PlayerID);
    }

    public static void removeAFKPlayer(UUID PlayerID) {
        AFKPlayers.remove(PlayerID);
    }

    public AFKOmega() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(RegisterCommandEvent.class);
        MinecraftForge.EVENT_BUS.register(RegisterInputEvent.class);

        // Item Registry
        RegistryEvents.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("AFK Omega has being its preinit setup.");
        AFKPlayers = new ArrayList<>();
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
        // some example code to receive and process InterModComms from other mods
        /*LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));*/
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("AFK Omega has begun.");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("AFK Omega is registering a new block...");
        }

        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AFKOmega.MODID);
    }
}