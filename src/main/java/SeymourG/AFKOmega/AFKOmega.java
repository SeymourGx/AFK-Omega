package SeymourG.AFKOmega;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("afkomega")
public class AFKOmega
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MODID = "afkomega";
    public static final int AFKINPUTCOOLDOWN = 2; // AFK Delay in SECONDS
    public static KeyBinding modKeyBinding;
    private static List<UUID> AFKPlayers;
    private static Map<UUID, Date> lastPlayerInputs;
    private static Map<UUID, List<Double>> lastPlayerLocations;
    private static Map<UUID, Integer> AFKDelays;

    public static void updatePlayerLocation(UUID uuid, List<Double> loc) {
        lastPlayerLocations.put(uuid, loc);
    }

    public static List<Double> getLastPlayerLocation(UUID uuid) {
        return lastPlayerLocations.get(uuid);
    }

    public static List<Double> getCurrentPlayerLocation(Entity entity) {
        ServerPlayerEntity player = getServerPlayerEntity(entity);
        if (player == null) {
            return getLastPlayerLocation(entity.getUniqueID());
        }
        List<Double> loc = new ArrayList<>();
        Double locX = player.lastTickPosX;
        Double locY = player.lastTickPosY;
        Double locZ = player.lastTickPosZ;
        if (locX != null && locY != null && locZ != null) {
            loc.add(locX);
            loc.add(locY);
            loc.add(locZ);
        }
        else {
            loc = getLastPlayerLocation(entity.getUniqueID());
        }

        return loc;
    }

    public static void LOG(String message, int severity) {
        // Logs message. For use by other classes.
        if (severity == 1) {
            // ERROR
            LOGGER.error(message);
        }
        else if (severity == 2) {
            // WARN
            LOGGER.warn(message);
        }
        else {
            // INFO
            LOGGER.info(message);
        }
    }

    public static void purgePlayer(UUID uuid) {
        // Removes UUID from all maps/lists, currently only on logout
        lastPlayerInputs.remove(uuid);
        AFKPlayers.remove(uuid);
        AFKDelays.remove(uuid);
    }

    public static Date getLastInput(UUID uuid) {
        return lastPlayerInputs.get(uuid);
    }

    public static void updatePlayerInput(UUID uuid, Date lastInputDate) {
        // Will replace the record if one exists, otherwise add new
        lastPlayerInputs.put(uuid, lastInputDate);
    }

    public static Map<UUID, Integer> getAFKDelays() {
        return AFKDelays;
    }

    public static void removeAFKDelay(UUID uuid) {
        AFKDelays.remove(uuid);
    }

    public static boolean checkAFKDelay(UUID uuid) {
        return AFKDelays.containsKey(uuid);
    }

    public static List<UUID> getAFKPlayers() {
        return AFKPlayers;
    }

    public static boolean isAFK(UUID uuid) {
        // Checks to see if the given UUID is currently AFK
        // This was added to avoid needing to pull the entire list of AFK Players
        return AFKPlayers.contains(uuid);
    }

    public static void addAFKPlayer(UUID PlayerID) {
        AFKPlayers.add(PlayerID);
    }

    public static void removeAFKPlayer(UUID PlayerID) {
        AFKPlayers.remove(PlayerID);
    }

    public static List<ServerPlayerEntity> getAllPlayers() {
        IntegratedServer server = Minecraft.getInstance().getIntegratedServer();
        if (server != null) {
            return server.getPlayerList().getPlayers();
        }
        else {
            return new ArrayList<>();
        }
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

    public static void sendMessageToPlayer (ServerPlayerEntity player, String message) {
        // Sends a message to the player from themselves
        StringTextComponent textComponent = new StringTextComponent(message);
        player.sendMessage(textComponent, player.getUniqueID());
    }

    public AFKOmega() {
        // Register these methods for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(AFKEventSubscribers.class);
    }

    public static void manualAFK(ServerPlayerEntity player) {
        // To be used when a player intentionally toggles AFK
        if (checkAFKDelay(player.getUniqueID())) {
            sendMessageToPlayer(player, "Please don't spam the AFK command/keybind.");
            return;
        }
        AFKDelays.put(player.getUniqueID(), AFKINPUTCOOLDOWN);
        ToggleAFK(player);
    }

    public static void ToggleAFK(ServerPlayerEntity player) {
        // Master command for toggling AFK on a player, whether automatic or manual
        List<ServerPlayerEntity> allPlayersList = getAllPlayers();
        String username = player.getDisplayName().toString();

        // Looping through all players and notifying them while updating AFK status of player
        for(ServerPlayerEntity tempPlayer : allPlayersList) {
            if (tempPlayer == player) {
                if (getAFKPlayers().contains(tempPlayer.getUniqueID())) {
                    sendMessageToPlayer(tempPlayer, "You are back from AFK.");
                    removeAFKPlayer(tempPlayer.getUniqueID());
                }
                else {
                    sendMessageToPlayer(tempPlayer, "You have gone AFK.");
                    addAFKPlayer(tempPlayer.getUniqueID());
                }
            } else {
                if (getAFKPlayers().contains(player.getUniqueID())) {
                    sendMessageToPlayer(tempPlayer, String.format("%s is back from AFK.", username));
                }
                else {
                    sendMessageToPlayer(tempPlayer, String.format("%s has gone AFK.", username));
                }
            }
        }
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // Initialization code
        LOGGER.info("AFK Omega has being its preinit setup.");
        AFKPlayers = new ArrayList<>();
        AFKDelays = new HashMap<>();
        lastPlayerInputs = new HashMap<>();
        lastPlayerLocations = new HashMap<>();
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // Handle Key Binding(s)
        modKeyBinding = new KeyBinding("Toggle AFK Status", 93, "AFK Omega");
        ClientRegistry.registerKeyBinding(modKeyBinding);
    }
}