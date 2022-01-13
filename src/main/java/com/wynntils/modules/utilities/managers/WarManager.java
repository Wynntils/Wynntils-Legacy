/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;

import net.minecraft.network.play.server.SPacketSpawnObject;

public class WarManager {

    /**
     * This filters the spawn of useless entities on wars
     * 78 == Armor Stands
     *
     * @param e the packet spawn event
     * @return if the mob should be filtered out
     */
    public static boolean filterMob(PacketEvent<SPacketSpawnObject> e) {
        if (!UtilitiesConfig.Wars.INSTANCE.allowEntityFilter || !Reference.onWars) return false;

        return e.getPacket().getType() == 78;
    }

}
