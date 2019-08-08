/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;


/**
 * triggered when a packet is sent to the client
 * is cancellable (you can avoid it to reach the client processor)
 *
 */
public class PacketEvent<T extends Packet<?>> extends GenericEvent<T> {
    
    T packet;
    NetHandlerPlayClient playClient;

    public PacketEvent(T packet, NetHandlerPlayClient playClient) {
        super((Class<T>) packet.getClass());
        this.packet = packet;
        this.playClient = playClient;
    }

    public T getPacket() {
        return packet;
    }

    public NetHandlerPlayClient getPlayClient() {
        return playClient;
    }

    public boolean isCancelable() {
        return true;
    }
}
