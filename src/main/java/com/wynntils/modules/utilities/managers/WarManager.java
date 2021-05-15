/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.network.play.client.CPacketUseEntity;
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

    /**
     * This blocks the user from clicking into workstations while warring
     * Works by blocking clicks at ArmorStands, which are responsible for the hitbox
     *
     * @param e the packet use entity event
     * @return if the click should be allowed
     */
    public static boolean allowClick(PacketEvent<CPacketUseEntity> e) {
        if (!UtilitiesConfig.Wars.INSTANCE.blockWorkstations || !Reference.onWars) return false;

        Entity in = e.getPacket().getEntityFromWorld(McIf.world());
        return in instanceof EntityArmorStand || in instanceof EntitySlime;
    }

}
