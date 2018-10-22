package cf.wynntils.core;

import cf.wynntils.core.events.ClientEvents;
import net.minecraftforge.common.MinecraftForge;

public class CoreManager {

    //not 100% sure

    public static void setupCore() {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

}
