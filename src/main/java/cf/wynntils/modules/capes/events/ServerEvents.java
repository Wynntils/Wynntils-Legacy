/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.capes.events;

import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.capes.managers.CapeManager;

public class ServerEvents implements Listener {

    @EventHandler
    public void joinServer(WynncraftServerEvent.Leave e){
        CapeManager.downloaded.clear();
    }

}
