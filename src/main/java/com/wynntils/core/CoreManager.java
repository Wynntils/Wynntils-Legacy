/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core;

import com.wynntils.McIf;
import com.wynntils.core.events.ClientEvents;
import com.wynntils.core.framework.enums.wynntils.WynntilsConflictContext;
import com.wynntils.core.framework.instances.PlayerInfo;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class CoreManager {

    /**
     * Called before all modules are registered
     */
    public static void preModules() {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        McIf.mc().gameSettings.keyBindForward.setKeyConflictContext(WynntilsConflictContext.ALLOW_MOVEMENTS);
        McIf.mc().gameSettings.keyBindBack.setKeyConflictContext(WynntilsConflictContext.ALLOW_MOVEMENTS);
        McIf.mc().gameSettings.keyBindRight.setKeyConflictContext(WynntilsConflictContext.ALLOW_MOVEMENTS);
        McIf.mc().gameSettings.keyBindLeft.setKeyConflictContext(WynntilsConflictContext.ALLOW_MOVEMENTS);
    }

    /**
     * Called after all modules are registered
     */
    public static void afterModules() {
        PlayerInfo.setup();
    }

}
