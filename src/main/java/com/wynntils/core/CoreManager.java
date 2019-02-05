/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core;

import com.wynntils.core.events.ClientEvents;
import net.minecraftforge.common.MinecraftForge;

public class CoreManager {

    public static void setupCore() {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

}
