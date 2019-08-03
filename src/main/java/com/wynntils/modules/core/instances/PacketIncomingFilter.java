/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.instances;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketIncomingFilter extends ChannelInboundHandlerAdapter {

    private static Minecraft mc = Minecraft.getMinecraft();

    /**
     * Dispatch a bunch of packet incoming events to be checked before reaching the interpretator
     * @see PacketEvent for more information about these events
     *
     *
     * @param ctx The Channel Handler
     * @param msg The incoming Packet
     * @throws Exception If something fails (idk exactly, that was inherited)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg == null) return;

        Event e = null;
        boolean cancel = false;

        if (msg instanceof SPacketSpawnObject) {
            e = new PacketEvent.SpawnObject((SPacketSpawnObject) msg, ModCore.mc().getConnection());
        } else if (msg instanceof  SPacketEntityMetadata) {
            e = new PacketEvent.EntityMetadata((SPacketEntityMetadata)msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketOpenWindow) {
            e = new PacketEvent.InventoryReceived((SPacketOpenWindow) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketWindowItems) {
            e = new PacketEvent.InventoryItemsReceived((SPacketWindowItems) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketResourcePackSend) {
            e = new PacketEvent.ResourcePackReceived((SPacketResourcePackSend) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketPlayerListItem) {
            e = new PacketEvent.TabListChangeEvent((SPacketPlayerListItem) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketTitle) {
            e = new PacketEvent.TitleEvent((SPacketTitle) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketSetExperience) {
            e = new PacketEvent.SetExperience((SPacketSetExperience) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketSpawnPosition) {
            e = new PacketEvent.SpawnPosition((SPacketSpawnPosition) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity velocity = (SPacketEntityVelocity) msg;
            if (mc.world != null) {
                Entity entity = mc.world.getEntityByID(velocity.getEntityID());
                Entity vehicle = mc.player.getLowestRidingEntity();
                if ((entity == vehicle) && (vehicle != mc.player) && (vehicle.canPassengerSteer())) {
                    cancel = true;
                }
            }
        } else if (msg instanceof SPacketMoveVehicle) {
            SPacketMoveVehicle moveVehicle = (SPacketMoveVehicle) msg;
            Entity vehicle = mc.player.getLowestRidingEntity();
            if ((vehicle == mc.player) || (!vehicle.canPassengerSteer()) || (vehicle.getDistance(moveVehicle.getX(), moveVehicle.getY(), moveVehicle.getZ()) <= 25D)) {
                cancel = true;
            }
        }


        if (e != null) cancel = MinecraftForge.EVENT_BUS.post(e);
        if (cancel) return;

        super.channelRead(ctx, msg);
    }

}
