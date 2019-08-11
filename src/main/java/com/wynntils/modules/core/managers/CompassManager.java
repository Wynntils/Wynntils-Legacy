/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.managers;


import com.wynntils.core.utils.Location;
import com.wynntils.modules.core.events.ServerEvents;
import net.minecraft.client.Minecraft;

public class CompassManager {

    private static Location compassLocation = null;

    public static Location getCompassLocation() {
        if(compassLocation != null) compassLocation.setY(Minecraft.getMinecraft().player.posY);
        return compassLocation;
    }

    public static void setCompassLocation(Location compassLocation) {
        CompassManager.compassLocation = compassLocation;

        Minecraft.getMinecraft().world.setSpawnPoint(compassLocation.toBlockPos());
    }

    public static void reset() {
        compassLocation = null;

        Minecraft.getMinecraft().world.setSpawnPoint(ServerEvents.getCurrentSpawnPosition());
    }

}
