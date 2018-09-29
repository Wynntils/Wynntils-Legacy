/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.instances;

import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.core.utils.Utils;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.*;
import net.minecraftforge.common.MinecraftForge;

public class PacketFilter extends NetHandlerPlayClient {

    public static PacketFilter instance;
    private static Minecraft mc = Minecraft.getMinecraft();

    public PacketFilter(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager networkManagerIn, GameProfile profileIn, NetHandlerPlayClient original) {
        super(mcIn, p_i46300_2_, networkManagerIn, profileIn);

        instance = this;
        try{
            Utils.copyInstance(original, this);
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void handleSpawnObject(SPacketSpawnObject packetIn) {
        PacketEvent.SpawnObject e = new PacketEvent.SpawnObject(packetIn, this);
        MinecraftForge.EVENT_BUS.post(e);

        if(e.isCanceled()) {
            return;
        }

        super.handleSpawnObject(packetIn);
    }

    @Override
    public void handleOpenWindow(SPacketOpenWindow packetIn) {
        PacketEvent.InventoryReceived e = new PacketEvent.InventoryReceived(packetIn, this);
        MinecraftForge.EVENT_BUS.post(e);

        if(e.isCanceled()) {
            return;
        }

        super.handleOpenWindow(packetIn);
    }

    @Override
    public void handleWindowItems(SPacketWindowItems packetIn) {
        PacketEvent.InventoryItemsReceived e = new PacketEvent.InventoryItemsReceived(packetIn, this);
        MinecraftForge.EVENT_BUS.post(e);

        if(e.isCanceled()) {
            return;
        }

        super.handleWindowItems(packetIn);
    }

    @Override
    public void handleResourcePack(SPacketResourcePackSend packet) {
        PacketEvent.ResourcePackReceived e = new PacketEvent.ResourcePackReceived(packet, getNetworkManager());
        MinecraftForge.EVENT_BUS.post(e);

        if(e.isCanceled()) {
            return;
        }

        super.handleResourcePack(packet);
    }

    @Override
    public void handleEntityVelocity(SPacketEntityVelocity packetIn) {
        Entity entity = mc.world.getEntityByID(packetIn.getEntityID());
        Entity vehicle = mc.player.getLowestRidingEntity();
        if ((entity != vehicle) || (vehicle == mc.player) || (!vehicle.canPassengerSteer())) {
            super.handleEntityVelocity(packetIn);
        }
    }

    @Override
    public void handleMoveVehicle(SPacketMoveVehicle packetIn) {
        Entity vehicle = mc.player.getLowestRidingEntity();
        if ((vehicle != mc.player) && (vehicle.canPassengerSteer())) {
            double x = packetIn.getX();
            double y = packetIn.getY();
            double z = packetIn.getZ();
            double d = vehicle.getDistance(x, y, z);
            if (d > 25.0D) {
                super.handleMoveVehicle(packetIn);
            }
        }
    }

}
