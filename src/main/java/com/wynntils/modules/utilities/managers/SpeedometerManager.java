/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.managers;

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

}
