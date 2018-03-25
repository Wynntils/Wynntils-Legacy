package cf.wynntils.modules.richpresence.events;

import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.richpresence.RichPresenceModule;
import cf.wynntils.modules.richpresence.overlays.LocationOverlay;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ChatEvents implements Listener {

    /**
     * Handlers for world login and entering notifier
     *
     * @param e
     *        Origem event
     */
    @EventHandler
    public void onChatReceive(ClientChatReceivedEvent e) {
        if(e.getMessage().getFormattedText().toLowerCase().contains("you are now entering") && !e.getMessage().getFormattedText().contains("/")) {
            if(RichPresenceModule.getMainConfig().enteringNotifier) {
                String loc = e.getMessage().getFormattedText();
                LocationOverlay.location = Utils.stripColor(loc.replace("[You are now entering ", "").replace("]", ""));
                e.setCanceled(true);
            }
            return;
        }
        if(e.getMessage().getFormattedText().toLowerCase().contains("you are now leaving") && !e.getMessage().getFormattedText().contains("/")) {
            if(RichPresenceModule.getMainConfig().enteringNotifier) {
                LocationOverlay.last_loc = "Waiting";
                LocationOverlay.location = "Waiting";
                e.setCanceled(true);
            }
        }
    }

}
