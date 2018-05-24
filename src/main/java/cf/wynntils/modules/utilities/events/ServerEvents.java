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
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.utilities.managers.TPSManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import org.lwjgl.opengl.Display;

public class ServerEvents implements Listener {

    public static boolean loadedResourcePack = false;

    @EventHandler
    public void leaveServer(WynncraftServerEvent.Leave e) {
        loadedResourcePack = false;
        Display.setTitle("Minecraft " + Minecraft.getMinecraft().getVersion());
    }

    @EventHandler
    public void joinServer(WynncraftServerEvent.Login e){
        Display.setTitle("Wynntils v" + Reference.VERSION);
    }

    @EventHandler
    public void onWorldLeft(WynnWorldLeftEvent e) {
        TPSManager.clearTpsInfo();
    }

    @EventHandler
    public void onWorldLeft(WynnWorldJoinEvent e) {
        TPSManager.clearTpsInfo();
    }

    @EventHandler
    public void onResourcePackReceive(PacketEvent.ResourcePackReceived e) {
        if(loadedResourcePack) {
            NetworkManager nm = e.getNetworkManager();
            nm.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            nm.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            e.setCanceled(true);
            return;
        }

        if(Reference.onServer) {
            loadedResourcePack = true;
        }
    }


}
