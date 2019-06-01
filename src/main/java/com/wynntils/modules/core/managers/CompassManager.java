/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.managers;


import com.wynntils.core.utils.Location;
import net.minecraft.client.Minecraft;

public class CompassManager {

    private static Location compassLocation = null;

    public static Location getCompassLocation() {
        return compassLocation;
    }

    public static void setCompassLocation(Location compassLocation) {
        CompassManager.compassLocation = compassLocation;

        Minecraft.getMinecraft().world.setSpawnPoint(compassLocation.toBlockPos());
    }

}
