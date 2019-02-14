/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.managers.WarManager;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerEvents implements Listener {

    private static boolean loadedResourcePack = false;

    @SubscribeEvent
    public void leaveServer(WynncraftServerEvent.Leave e) {
        loadedResourcePack = false;
    }

    @SubscribeEvent
    public void onResourcePackReceive(PacketEvent.ResourcePackReceived e) {
        if(!Reference.onServer) return;

        if(loadedResourcePack) {
            e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));

            e.setCanceled(true);
            return;
        }

        loadedResourcePack = true;
    }

    @SubscribeEvent
    public void onSpawnObject(PacketEvent.SpawnObject e) {
        if(WarManager.filterMob(e)) e.setCanceled(true);
    }


}
