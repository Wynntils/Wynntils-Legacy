/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.core.utils.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.MathHelper;

public class SpeedometerManager {

    private static long nextUpdate = 0;
    private static Location lastLocation = null;

    public static double getCurrentSpeed() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        double distX = player.posX - player.prevPosX;
        double distZ = player.posZ - player.prevPosZ;

        return (MathHelper.sqrt((distX * distX) + (distZ * distZ))) * 20d;
    }

}
