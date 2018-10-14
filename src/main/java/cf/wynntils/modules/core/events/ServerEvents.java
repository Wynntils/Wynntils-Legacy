/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.events;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.core.instances.PacketFilter;
import cf.wynntils.webapi.WebManager;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ServerEvents implements Listener {

    @EventHandler
    public void joinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        e.getManager().channel().pipeline().addBefore("fml:packet_handler", Reference.MOD_ID + ":packet_filter", new PacketFilter());

        WebManager.checkForUpdates();
    }
}
