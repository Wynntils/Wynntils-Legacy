/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.containers.PartyContainer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class WynnSocialEvent extends Event {

    /**
     * Triggered when the users leaves a Party
     */
    public static class PartyLeave extends WynnSocialEvent { }

    /**
     * Triggered when the users joins a Party
     */
    public static class PartyJoin extends WynnSocialEvent {

        public PartyContainer getParty() {
            return PlayerInfo.getPlayerInfo().getPlayerParty();
        }

    }

}
