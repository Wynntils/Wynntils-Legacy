/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.core.instances;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.FrameworkManager;
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
        if (packet == FakeInventory.ignoredPacket) {
            noEvent = true;
        }

        if (!noEvent && FrameworkManager.getEventBus().post(new PacketEvent<Packet<?>>(packet, ModCore.mc().getConnection()))) return;

        super.write(ctx, msg, promise);
    }

}
