/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.managers;

import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;

public class WarManager {

    /**
     * This filters the spawn of useless entities on wars
     * 78 == Armor Stands
     *
     * @param e the packet spawn event
     * @return if the mob should be filtered out
     */
    public static boolean filterMob(PacketEvent.SpawnObject e) {
        if(!UtilitiesConfig.Wars.INSTANCE.allowEntityFilter) return false;
        if(!Reference.onWars) return false;
        if(e.getPacket().getType() == 78) return true;

        return false;
    }

}
