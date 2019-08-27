/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.managers;

import com.wynntils.modules.core.instances.PacketResponse;
import net.minecraft.network.Packet;

import java.util.ArrayList;
import java.util.function.Function;

public class PacketQueue {

    private static ArrayList<PacketResponse> complexQueue = new ArrayList<>();

    public static void queueSimplePacket(Packet<?> packet) {
        complexQueue.add(new PacketResponse(packet));
    }

    public static void queueComplexPacket(Packet<?> packet, Class<?> responseType) {
        complexQueue.add(new PacketResponse(packet, responseType));
    }

    public static void queueComplexPacket(Packet<?> packet, Class<?> responseType, Function<Packet<?>, Boolean> verification) {
        PacketResponse response = new PacketResponse(packet, responseType);
        response.setVerification(verification);

        complexQueue.add(response);
    }

    public static void checkResponse(Packet<?> response) {
        if(complexQueue.size() == 0) return;

        PacketResponse r = complexQueue.get(0);
        if(!r.isResponseValid(response)) return;

        complexQueue.remove(0);
    }

    public static void proccessQueue() {
        if(complexQueue.isEmpty()) return;

        complexQueue.get(0).sendPacket();
    }

}
