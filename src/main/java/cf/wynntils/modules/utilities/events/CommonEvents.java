package cf.wynntils.modules.utilities.events;

import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.WynnWorldJoinEvent;
import cf.wynntils.core.events.custom.WynnWorldLeftEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.utilities.managers.TPSManager;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class CommonEvents implements Listener {

    @EventHandler
    public void onWorldLeft(WynnWorldLeftEvent e) {
        TPSManager.clearTpsInfo();
    }

    @EventHandler
    public void onWorldLeft(WynnWorldJoinEvent e) {
        TPSManager.clearTpsInfo();
    }

    @EventHandler
    public void clientTick(TickEvent.ClientTickEvent e) {
        if(Reference.onWorld) {
            TPSManager.updateTps();
        }
    }

}
