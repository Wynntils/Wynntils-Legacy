package cf.wynntils.modules.utilities.overlays;

import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.core.events.custom.WynnWorldJoinEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.modules.utilities.overlays.hud.WarTimerOverlay;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class OverlayEvents implements Listener {
    
    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent e) {
        WarTimerOverlay.warMessage(e);
    }
    
    @SubscribeEvent
    public void onWorldJoin(WynnWorldJoinEvent e) {
        WarTimerOverlay.onWorldJoin(e);
    }
    
    @SubscribeEvent
    public void onTitle(PacketEvent.TitleEvent e) {
        WarTimerOverlay.onTitle(e);
    }

}
