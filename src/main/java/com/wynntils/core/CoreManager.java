/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core;

import com.wynntils.core.events.ClientEvents;
import com.wynntils.core.framework.enums.WynntilsConflictContext;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class CoreManager {

    public static void setupCore() {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        Minecraft.getMinecraft().gameSettings.keyBindForward.setKeyConflictContext(WynntilsConflictContext.ALLOW_MOVEMENTS);
        Minecraft.getMinecraft().gameSettings.keyBindBack.setKeyConflictContext(WynntilsConflictContext.ALLOW_MOVEMENTS);
        Minecraft.getMinecraft().gameSettings.keyBindRight.setKeyConflictContext(WynntilsConflictContext.ALLOW_MOVEMENTS);
        Minecraft.getMinecraft().gameSettings.keyBindLeft.setKeyConflictContext(WynntilsConflictContext.ALLOW_MOVEMENTS);
    }

}
