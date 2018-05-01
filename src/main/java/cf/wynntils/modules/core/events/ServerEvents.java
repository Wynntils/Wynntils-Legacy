/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.events;

import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.core.instances.PacketFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ServerEvents implements Listener {

    @EventHandler
    public void joinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        NetworkManager nm = e.getManager();
        NetHandlerPlayClient nhpc = (NetHandlerPlayClient)nm.packetListener;
        nm.packetListener = new PacketFilter(Minecraft.getMinecraft(), nhpc.guiScreenServer, nm, nhpc.profile, nhpc);
    }

}
