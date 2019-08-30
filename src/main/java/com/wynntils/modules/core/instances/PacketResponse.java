/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.instances;

import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.managers.PingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import java.util.function.Function;

public class PacketResponse {

    Packet input;
    Class responseType;

    Function<Packet, Boolean> verification = null;

    long lastSent = -1;
    int tries = 0;

    public PacketResponse(Packet input, Class responseType) {
        this.input = input;
        this.responseType = responseType;
    }

    public PacketResponse(Packet input) {
        this.input = input;

        this.responseType = null;
    }

    public Packet getInput() {
        return input;
    }

    public Class getResponseType() {
        return responseType;
    }

    public void setVerification(Function<Packet, Boolean> verification) {
        this.verification = verification;
    }

    public boolean shouldSend() {
        return lastSent == -1 || System.currentTimeMillis() - lastSent > PingManager.getLastPing() + 150;
    }

    //TODO make this verification faster cuz at the current state it's slowing the packet a lot
    public boolean isResponseValid(Packet packetType) {
        if(responseType == null || tries >= 3) return true; //this avoids packet spamming
        if(!packetType.getClass().isAssignableFrom(responseType)) return false;

        return verification == null || verification.apply(packetType);
    }

    public void sendPacket() {
        if(!shouldSend()) return;

        Utils.runAsync(() -> {
            Minecraft.getMinecraft().getConnection().sendPacket(input);
            lastSent = System.currentTimeMillis();
            tries++;
        });
    }

}
