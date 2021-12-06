package SeymourG.AFKOmega;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.*;

public class AFKEventHandlers {
    private static final int SECONDS_UNTIL_AFK = 5; // AFK time in seconds until triggering AFK status

    protected static void afkCooldown() {
        // RUNS PER SECOND
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

    protected static void logInput() {
        // RUNS ON EVERY INPUT IN-GAME
        // Log the time of this input
        ClientPlayerEntity player = Minecraft.getInstance().player;
        UUID uuid = player.getUniqueID();
        AFKOmega.updatePlayerInput(uuid, new Date());

        // Log the location of the Player
        List<Double> loc = AFKOmega.getCurrentPlayerLocation(player.getEntity());
        AFKOmega.updatePlayerLocation(uuid, loc);
    }

    protected static void automaticAFK() {
        // RUNS PER SECOND
        // Loop through all players and check their last input time
        AFKOmega.LOG("Performing automatic AFK check...", 0);
        List<ServerPlayerEntity> allPlayers = AFKOmega.getAllPlayers();
        Date currentTime = new Date();

        for (ServerPlayerEntity player : allPlayers) {
            if (AFKOmega.isAFK(player.getUniqueID())) {
                // No need to try and trigger AFK on someone already AFK
                return;
            }

            // Getting difference between now and the last input time
            Date lastInputTime = AFKOmega.getLastInput(player.getUniqueID());
            if (lastInputTime == null) {
                // This should never be possible, but for now I'm leaving this in to flag if it does
                AFKOmega.LOG(String.format("AFK Omega, Automatic AFK Check: lastInputTime was null for %s at %s", player.getDisplayName(), currentTime), 2);
            }
            // Directly subtracting the two dates.getTime() results in milliseconds, so converting to seconds
            long differenceInSeconds = (currentTime.getTime() - lastInputTime.getTime()) / 1000;
            if (differenceInSeconds >= SECONDS_UNTIL_AFK) {
                // User has been AFK too long, triggering status
                AFKOmega.ToggleAFK(player);
            }
        }
    }

    protected static void checkIfPlayerMovedWhileAFK(Entity entity) {
        // RUNS PER INPUT
        // Turns AFK off if the player provided input AND MOVED while AFK
        UUID uuid = entity.getUniqueID();
        if (AFKOmega.isAFK(uuid)) {
            // Player is AFK, now to check whether they've moved while AFK
            List<Double> currentLoc = AFKOmega.getCurrentPlayerLocation(entity);
            List<Double> lastLoc = AFKOmega.getLastPlayerLocation(uuid);
            if (!currentLoc.equals(lastLoc)) {
                // Player has moved while AFK, toggling AFK status
                AFKOmega.LOG(String.format("Player has moved from %s to %s while AFK. Toggling AFK status.", lastLoc, currentLoc), 0);
                ServerPlayerEntity player = AFKOmega.getServerPlayerEntity(entity);
                AFKOmega.ToggleAFK(player);
            }
        }
    }
}
