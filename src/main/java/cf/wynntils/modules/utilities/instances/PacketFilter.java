/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.instances;

import cf.wynntils.Reference;
import cf.wynntils.core.utils.Utils;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.server.SPacketResourcePackSend;

public class PacketFilter extends NetHandlerPlayClient {

    //handlers
    public static boolean loadedResourcePack = false;

    public PacketFilter(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager networkManagerIn, GameProfile profileIn, NetHandlerPlayClient original) {
        super(mcIn, p_i46300_2_, networkManagerIn, profileIn);

        try{
            Utils.copyInstance(original, this);
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void handleResourcePack(SPacketResourcePackSend packet) {
        if(loadedResourcePack) {
            NetworkManager nm = getNetworkManager();
            nm.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
            nm.sendPacket(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            return;
        }

        if(Reference.onServer) {
            loadedResourcePack = true;
        }
        super.handleResourcePack(packet);
    }

}
