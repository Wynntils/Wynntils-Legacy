/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.managers.CompassManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;

public class SpeedometerManager {

    public static double getCurrentSpeed() {
        EntityPlayerSP player = McIf.player();

        double distX = player.posX - player.prevPosX;
        double distZ = player.posZ - player.prevPosZ;

        return (MathHelper.sqrt((distX * distX) + (distZ * distZ))) * 20d;
    }

    public static int getTravelingTime() {
        Location compass = CompassManager.getCompassLocation();
        if (compass != null) {
            Location playerPos = new Location(McIf.player());
            double distance = compass.distance(playerPos);
            double bps = getCurrentSpeed();
            if (bps != 0) {
                return (int) (distance / bps);
            }
        }
        return 0;
    }
}
