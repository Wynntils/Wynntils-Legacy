/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.HorseData;
import com.wynntils.core.utils.helpers.Delay;
import com.wynntils.modules.utilities.events.ClientEvents;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;

import java.sql.Ref;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MountHorseManager {

    public enum MountHorseStatus {
        SUCCESS, ALREADY_RIDING, NO_HORSE, SPAWNING
    }

    private static final int searchRadius = 18;  // Search a bit further for message "too far" instead of "not found"
    private static final int remountTickDelay = 5;
    private static final int spawnAttempts = 8;

    private static boolean ingamePrevention = false;

    public static boolean isPlayersHorse(Entity horse, String playerName) {
        return (horse instanceof AbstractHorse) && isPlayersHorse(horse.getCustomNameTag(), playerName);
    }

    public static boolean isPlayersHorse(String horseName, String playerName) {
        String defaultName = TextFormatting.WHITE + playerName + TextFormatting.GRAY + "'s horse";
        String customSuffix = TextFormatting.GRAY + " [" + playerName + "]";

        return defaultName.equals(horseName) || horseName.endsWith(customSuffix);
    }

    private static Entity findHorseInRadius(Minecraft mc) {
        EntityPlayerSP player = mc.player;

        List<Entity> horses = mc.world.getEntitiesWithinAABB(AbstractHorse.class, new AxisAlignedBB(
                player.posX - searchRadius, player.posY - searchRadius, player.posZ - searchRadius,
                player.posX + searchRadius, player.posY + searchRadius, player.posZ + searchRadius
        ));

        String playerName = player.getName();

        for (Entity h : horses) {
            if (isPlayersHorse(h, playerName)) {
                return h;
            }
        }
        return null;
    }

    private static void tryDelayedSpawnMount(Minecraft mc, HorseData horse, int attempts) {
        if (ingamePrevention) {
            ingamePrevention = false;
            return;
        }

        if (attempts <= 0) {
            String message = getMountHorseErrorMessage(MountHorseStatus.NO_HORSE);
            GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + message);
            ingamePrevention = false;
            return;
        }

        int prev = mc.player.inventory.currentItem;
        new Delay(() -> {
            mc.player.inventory.currentItem = horse.getInventorySlot();
            mc.playerController.processRightClick(mc.player, mc.player.world, EnumHand.MAIN_HAND);
            mc.player.inventory.currentItem = prev;

            if (findHorseInRadius(mc) != null) {
                ClientEvents.isAwaitingHorseMount = true;
                return;
            }
            tryDelayedSpawnMount(mc, horse, attempts - 1);
        }, remountTickDelay);
    }

    public static void preventNextMount() {
        ingamePrevention = true;
    }

    public static MountHorseStatus mountHorse(boolean allowRetry) {
        Minecraft mc = ModCore.mc();
        EntityPlayerSP player = mc.player;
        PlayerControllerMP playerController = mc.playerController;

        HorseData horse = PlayerInfo.get(HorseData.class);

        if (!horse.hasHorse() || horse.getInventorySlot() > 8) {
            return MountHorseStatus.NO_HORSE;
        }

        if (player.isRiding()) {
            return MountHorseStatus.ALREADY_RIDING;
        }

        Entity playersHorse = findHorseInRadius(mc);

        int prev = player.inventory.currentItem;
        boolean far = false;
        if (playersHorse != null) {
            double maxDistance = player.canEntityBeSeen(playersHorse) ? 36.0D : 9.0D;
            far = player.getDistanceSq(playersHorse) > maxDistance;
        }

        if (playersHorse == null || far) {
            if (!allowRetry) {
                return MountHorseStatus.NO_HORSE;
            }

            player.inventory.currentItem = horse.getInventorySlot();
            playerController.processRightClick(player, player.world, EnumHand.MAIN_HAND);
            player.inventory.currentItem = prev;
            if (far) {
                tryDelayedSpawnMount(mc, horse, spawnAttempts);
                return MountHorseStatus.SPAWNING;
            }
            if (ingamePrevention) {
                ingamePrevention = false;
            } else {
                ClientEvents.isAwaitingHorseMount = true;
            }
            return MountHorseStatus.SUCCESS;

        }

        player.inventory.currentItem = 8; // swap to soul points to avoid any right-click conflicts
        playerController.interactWithEntity(player, playersHorse, EnumHand.MAIN_HAND);
        player.inventory.currentItem = prev;
        return MountHorseStatus.SUCCESS;
    }

    public static String getMountHorseErrorMessage(MountHorseStatus status) {
        switch (status) {
            case ALREADY_RIDING:
                Entity ridingEntity = ModCore.mc().player.getRidingEntity();
                String ridingEntityType;
                if (ridingEntity == null) {
                    ridingEntityType = "nothing?";
                } else if (ridingEntity instanceof AbstractHorse) {
                    ridingEntityType = "a horse";
                } else if (ridingEntity instanceof EntityBoat) {
                    ridingEntityType = "a boat";
                } else {
                    String name = ridingEntity.getName();
                    if (name == null) {
                        ridingEntityType = "something";
                    } else {
                        ridingEntityType = name;
                    }
                }
                return "You are already riding " + ridingEntityType;
            case NO_HORSE:
                return "Your horse was unable to be found";
            default:
                return null;
        }

    }

    // Called on key press
    public static void mountHorseAndShowMessage() {
        String message = getMountHorseErrorMessage(mountHorse(true));
        if (message == null) return;

        GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + message);
    }

    // Called by event when a horse's metadata (name) is sent
    public static void mountHorseAndLogMessage() {
        String message = MountHorseManager.getMountHorseErrorMessage(MountHorseManager.mountHorse(true));
        if (message == null) return;

        Reference.LOGGER.warn("mountHorse failed onHorseSpawn. Reason: " + message);
    }

    // Called post horse spawn after key press
    public static void retryMountHorseAndShowMessage() {
        String message = getMountHorseErrorMessage(mountHorse(false));
        if (message == null) return;

        GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + message);
    }

}
