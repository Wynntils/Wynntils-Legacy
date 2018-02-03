package cf.wynntils.core;

import cf.wynntils.core.events.ClientEvents;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class CoreManager {

    public static void setupCore() {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        
    }

}
