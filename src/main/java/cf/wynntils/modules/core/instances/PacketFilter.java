/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.instances;

import cf.wynntils.ModCore;
import cf.wynntils.core.events.custom.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketFilter extends ChannelInboundHandlerAdapter {

    private static Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Event e = null;
        boolean cancel = false;
        if (msg instanceof SPacketSpawnObject) {
            e = new PacketEvent.SpawnObject((SPacketSpawnObject) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketOpenWindow) {
            e = new PacketEvent.InventoryReceived((SPacketOpenWindow) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketWindowItems) {
            e = new PacketEvent.InventoryItemsReceived((SPacketWindowItems) msg, ModCore.mc().getConnection());
        } else if (msg instanceof SPacketResourcePackSend) {
            e = new PacketEvent.ResourcePackReceived((SPacketResourcePackSend) msg, ModCore.mc().getConnection().getNetworkManager());
        } else if (msg instanceof SPacketPlayerListItem) {
            e = new PacketEvent.TabListChangeEvent((SPacketPlayerListItem) msg, ModCore.mc().getConnection().getNetworkManager());
        } else if (msg instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity velocity = (SPacketEntityVelocity) msg;
            Entity entity = mc.world.getEntityByID(velocity.getEntityID());
            Entity vehicle = mc.player.getLowestRidingEntity();
            if ((entity == vehicle) && (vehicle != mc.player) && (vehicle.canPassengerSteer())) {
                cancel = true;
            }
        } else if (msg instanceof SPacketMoveVehicle) {
            SPacketMoveVehicle moveVehicle = (SPacketMoveVehicle) msg;
            Entity vehicle = mc.player.getLowestRidingEntity();
            if ((vehicle == mc.player) || (!vehicle.canPassengerSteer()) || (vehicle.getDistance(moveVehicle.getX(), moveVehicle.getY(), moveVehicle.getZ()) <= 25D)) {
                cancel = true;
            }
        }
        if (e != null) {
            MinecraftForge.EVENT_BUS.post(e);

            cancel = e.isCanceled();
        }
        if (cancel) {
            return;
        }
        super.channelRead(ctx, msg);
    }

}
