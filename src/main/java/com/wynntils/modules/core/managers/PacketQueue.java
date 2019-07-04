/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.managers;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class PacketQueue {

    private static Queue<Packet> packetQueue = new ArrayDeque<>();

    public static void queuePacket(Packet<?> packet) {
        packetQueue.add(packet);
    }

    public static void queuePackets(Packet<?>... packets) {
        packetQueue.addAll(Arrays.asList(packets));
    }

    public static void proccessQueue() {
        if(packetQueue.isEmpty()) return;

        Minecraft.getMinecraft().getConnection().sendPacket(packetQueue.poll());
    }

}
