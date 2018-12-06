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
import cf.wynntils.webapi.WebManager;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerEvents implements Listener {

    public static int loadedResourcePack = 0;
    
    public static String loadedResourcePackURL = "";

    @SubscribeEvent
    public void leaveServer(WynncraftServerEvent.Leave e) {
        loadedResourcePack = 0;
        loadedResourcePackURL = "";
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
        if (!e.getPacket().getURL().equals(loadedResourcePackURL)) {
            loadedResourcePack = 0;
            loadedResourcePackURL = e.getPacket().getURL();
        }
        
        if(loadedResourcePack >= Integer.valueOf(WebManager.apiUrls.get("ResourcePackUpdateAmount"))) {
            NetworkManager nm = e.getNetworkManager();
            nm.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            nm.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            e.setCanceled(true);
            return;
        }

        if(Reference.onServer) {
            loadedResourcePack+= 1;
        }
    }

    @SubscribeEvent
    public void onSpawnObject(PacketEvent.SpawnObject e) {
        if(WarManager.filterMob(e)) e.setCanceled(true);
    }


}
