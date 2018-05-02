/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.events;

import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.core.utils.ReflectionFields;
import cf.wynntils.modules.core.instances.PacketFilter;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ServerEvents implements Listener {

    @EventHandler
    public void joinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        NetworkManager nm = e.getManager();
        NetHandlerPlayClient client = (NetHandlerPlayClient) ReflectionFields.NetworkManager_packetListener.getValue(nm);
        ReflectionFields.NetworkManager_packetListener.setValue(nm, new PacketFilter(Minecraft.getMinecraft(), (GuiScreen)ReflectionFields.NetHandlerPlayClient_guiScreenServer.getValue(client), nm, (GameProfile)ReflectionFields.NetHandlerPlayClient_profile.getValue(client), client));
    }

}
