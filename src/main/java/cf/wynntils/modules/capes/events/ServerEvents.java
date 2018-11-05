/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.capes.events;

import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.modules.capes.managers.CapeManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerEvents implements Listener {

    @SubscribeEvent
    public void joinServer(WynncraftServerEvent.Leave e){
        CapeManager.downloaded.clear();
    }

}
