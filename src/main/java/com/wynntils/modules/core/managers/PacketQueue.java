/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.core.managers;

import com.wynntils.modules.core.instances.packet.PacketResponse;
import net.minecraft.network.Packet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PacketQueue {

    private static List<PacketResponse> complexQueue = new ArrayList<>();

    public static PacketResponse queueSimplePacket(Packet packet) {
        PacketResponse response = new PacketResponse(packet);
        complexQueue.add(response);
        return response;
    }

    public static PacketResponse queueComplexPacket(Packet packet, Class responseType) {
        PacketResponse response = new PacketResponse(packet, responseType);
        complexQueue.add(response);
        return response;
    }

    public static PacketResponse queueComplexPacket(Packet packet, Class responseType, Function<Packet, Boolean> verification) {
        PacketResponse response = new PacketResponse(packet, responseType);
        response.setVerification(verification);

        complexQueue.add(response);
        return response;
    }

    public static void checkResponse(Packet<?> response) {
        if (complexQueue.size() == 0) return;

        PacketResponse r = complexQueue.get(0);
        if (!r.isResponseValid(response)) return;

        complexQueue.remove(0);
    }

    public static boolean hasQueuedPacket() {
        return !complexQueue.isEmpty();
    }

    public static void proccessQueue() {
        if (complexQueue.isEmpty()) return;

        complexQueue.get(0).sendPacket();
    }

}
