/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.instances;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.FrameworkManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraftforge.fml.common.eventhandler.Event;

public class OutgoingFilter extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Event e = null;

        if(msg instanceof CPacketPlayerDigging) {
            CPacketPlayerDigging packet = (CPacketPlayerDigging) msg;
            if(packet.getAction() == CPacketPlayerDigging.Action.DROP_ITEM || packet.getAction() == CPacketPlayerDigging.Action.DROP_ALL_ITEMS)
                e = new PacketEvent.PlayerDropItemEvent(packet, ModCore.mc().getConnection());
        }else if(msg instanceof CPacketPlayerTryUseItemOnBlock) {
            e = new PacketEvent.PlayerUseItemOnBlockEvent((CPacketPlayerTryUseItemOnBlock)msg, ModCore.mc().getConnection());
        }else if(msg instanceof CPacketPlayerTryUseItem) {
            e = new PacketEvent.PlayerUseItemEvent((CPacketPlayerTryUseItem)msg, ModCore.mc().getConnection());
        }

        if(e != null && FrameworkManager.getEventBus().post(e)) return;

        super.write(ctx, msg, promise);
    }

}
