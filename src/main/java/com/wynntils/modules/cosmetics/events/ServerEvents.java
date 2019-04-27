/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.cosmetics.events;

import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.cosmetics.managers.CapeManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerEvents implements Listener {

    @SubscribeEvent
    public void joinServer(WynncraftServerEvent.Leave e){
        CapeManager.downloaded.clear();
    }

}
