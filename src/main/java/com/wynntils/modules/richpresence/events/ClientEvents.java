/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.WynnSocialEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.richpresence.RichPresenceModule;
import com.wynntils.modules.richpresence.profiles.SecretContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent
    public void onJoinParty(WynnSocialEvent.Party.Join e) {
        RichPresenceModule.getModule().getRichPresence().setJoinSecret(new SecretContainer(e.getParty().getOwner(), Reference.getUserWorld().replaceAll("\\d+", ""), Integer.parseInt(Reference.getUserWorld().replace("WC", "").replace("HB", ""))));
    }

    @SubscribeEvent
    public void onLeaveParty(WynnSocialEvent.Party.Leave e) {
        RichPresenceModule.getModule().getRichPresence().setJoinSecret(null);
    }

    @SubscribeEvent
    public void onLeaveWorld(WynnWorldEvent.Leave e) {
        RichPresenceModule.getModule().getRichPresence().setJoinSecret(null);
    }

    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.START) return;

        RichPresenceModule.getModule().getRichPresence().runCallbacks();
    }

}
