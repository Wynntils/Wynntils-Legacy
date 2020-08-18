/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.music.managers;

import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;


public class BossTrackManager {

    private static int bossEntityId = -1;

    public static void update() {
        if (bossEntityId == -1) return;

        // check if the boss is still alive
        Entity in = Minecraft.getMinecraft().world.getEntityByID(bossEntityId);
        if (in != null && in.getDistance(Minecraft.getMinecraft().player) <= 20) return;

        bossEntityId = -1;
        SoundTrackManager.getPlayer().getStatus().setStopping(true);
    }

    public static void checkEntity(Entity entity, String name) {
        String soundTrack = WebManager.getMusicLocations().getBossTrack(name);
        if (soundTrack == null || entity.getDistance(Minecraft.getMinecraft().player) > 20) return;

        bossEntityId = entity.getEntityId();
        SoundTrackManager.findTrack(soundTrack, true);
    }

    public static boolean isAlive() {
        return bossEntityId != -1;
    }

}
