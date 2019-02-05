/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.capes.events;

import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.capes.managers.CapeManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerEvents implements Listener {

    @SubscribeEvent
    public void joinServer(WynncraftServerEvent.Leave e){
        CapeManager.downloaded.clear();
    }

}
