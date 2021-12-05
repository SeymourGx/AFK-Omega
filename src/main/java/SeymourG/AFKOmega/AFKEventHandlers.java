package SeymourG.AFKOmega;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AFKEventHandlers {
    private static final int SECONDS_UNTIL_AFK = 5; // AFK time in seconds until triggering AFK status

    static void afkCooldown() {
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

    static void logInput() {
        // RUNS ON EVERY INPUT
        // Log the time of this input
        UUID uuid = Minecraft.getInstance().player.getUniqueID();
        AFKOmega.updatePlayerInput(uuid, new Date());
    }

    static void automaticAFK() {
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

    static void noLongerAFK(Entity entity) {
        // RUNS PER INPUT
        // Turns AFK off if the player provided input while AFK
        if (AFKOmega.isAFK(entity.getUniqueID())) {
            ServerPlayerEntity player = AFKOmega.getServerPlayerEntity(entity);
            AFKOmega.ToggleAFK(player);
        }
    }
}
