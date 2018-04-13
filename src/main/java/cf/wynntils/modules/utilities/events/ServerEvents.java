/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.events;

import cf.wynntils.core.events.custom.WynnWorldJoinEvent;
import cf.wynntils.core.events.custom.WynnWorldLeftEvent;
import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.utilities.instances.PacketFilter;
import cf.wynntils.modules.utilities.managers.TPSManager;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.lang.reflect.Field;

public class ServerEvents implements Listener {

    @EventHandler
    public void joinServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {

        //apply packet filter
        try{
            NetworkManager nm = e.getManager();
            Field field = NetworkManager.class.getDeclaredFields()[12];
            field.setAccessible(true);
            NetHandlerPlayClient nhpc = (NetHandlerPlayClient)field.get(nm);
            Field[] fields = NetHandlerPlayClient.class.getDeclaredFields();
            field = fields[3];
            field.setAccessible(true);
            GuiScreen gui = (GuiScreen)field.get(nhpc);
            field = fields[2];
            field.setAccessible(true);
            GameProfile prof = (GameProfile)field.get(nhpc);
            NetHandlerPlayClient nnhpc = new PacketFilter(Minecraft.getMinecraft(), gui, nm, prof, nhpc);
            field = NetworkManager.class.getDeclaredFields()[12];
            field.setAccessible(true);
            field.set(nm, nnhpc);
        }catch (Exception ex) { ex.printStackTrace();}
    }

    @EventHandler
    public void leaveServer(WynncraftServerEvent.Leave e) {
        PacketFilter.loadedResourcePack = false;
    }

    @EventHandler
    public void onWorldLeft(WynnWorldLeftEvent e) {
        TPSManager.clearTpsInfo();
    }

    @EventHandler
    public void onWorldLeft(WynnWorldJoinEvent e) {
        TPSManager.clearTpsInfo();
    }

}
