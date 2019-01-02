/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.events;

import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.core.events.custom.WynnWorldJoinEvent;
import cf.wynntils.core.events.custom.WynnWorldLeftEvent;
import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.modules.utilities.managers.TPSManager;
import cf.wynntils.modules.utilities.managers.WarManager;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerEvents implements Listener {

    private static boolean loadedResourcePack = false;
    private static long lastResourcePack = 0;

    @SubscribeEvent
    public void leaveServer(WynncraftServerEvent.Leave e) {
        loadedResourcePack = false;
    }

    @SubscribeEvent
    public void onWorldLeft(WynnWorldLeftEvent e) {
        TPSManager.clearTpsInfo();
    }

    @SubscribeEvent
    public void onWorldLeft(WynnWorldJoinEvent e) {
        TPSManager.clearTpsInfo();
    }

    @SubscribeEvent
    public void onResourcePackReceive(PacketEvent.ResourcePackReceived e) {
        if(!Reference.onServer) return;

        if(loadedResourcePack) {
            if(System.currentTimeMillis() - lastResourcePack >= 2500) {
                e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                e.getPlayClient().sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            }

            lastResourcePack = System.currentTimeMillis();
            e.setCanceled(true);
            return;
        }

        lastResourcePack = System.currentTimeMillis();
        loadedResourcePack = true;
    }

    @SubscribeEvent
    public void onSpawnObject(PacketEvent.SpawnObject e) {
        if(WarManager.filterMob(e)) e.setCanceled(true);
    }


}
