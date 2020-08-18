/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.music.managers;

import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;

public class BossTrackManager {

    private static int bossEntityId = -1;

    public static void update() {
        if (bossEntityId == -1) return;

        // check if the boss is still alive
        boolean alive = Minecraft.getMinecraft().world.getEntityByID(bossEntityId) != null;
        if (alive) return;

        bossEntityId = -1;
        SoundTrackManager.getPlayer().getStatus().setStopping(true);
    }

    public static void checkEntity(int entityId, String name) {
        String soundTrack = WebManager.getMusicLocations().getBossTrack(name);
        if (soundTrack == null) return;

        bossEntityId = entityId;
        SoundTrackManager.findTrack(soundTrack, true);
    }

    public static boolean isAlive() {
        return bossEntityId != -1;
    }

}
