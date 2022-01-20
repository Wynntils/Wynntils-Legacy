/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.instances.packet;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.modules.core.instances.inventory.InventoryOpenByItem;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Packet;

public class PacketOutgoingFilter extends ChannelOutboundHandlerAdapter {

    /**
     * Dispatch a packet outgoing event to be checked before actually being sent
     *
     * @see PacketEvent for more information about these events
     *
     *
     * @param ctx The Channel Handler
     * @param msg The incoming Packet
     * @throws Exception If something fails (idk exactly, that was inherited)
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        boolean noEvent = false;

        Packet<?> packet = (Packet<?>) msg;
        if (packet == InventoryOpenByItem.ignoredPacket) {
            noEvent = true;
        }

        PacketEvent.Outgoing<? extends Packet<?>> event = noEvent ? null :
            new PacketEvent.Outgoing<>(packet, McIf.mc().getConnection(), this, ctx);
        if (!noEvent && FrameworkManager.getEventBus().post(event)) return;

        super.write(ctx, msg, promise);
    }

}
