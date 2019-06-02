/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class QuickCastManager implements Listener {

    public static ArrayList<Packet> packetQueue = new ArrayList<>();

    public static void castFirstSpell() {
        if(!canCastSpell()) return;

        NetHandlerPlayClient client = Minecraft.getMinecraft().getConnection();
        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
            packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
            packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
            return;
        }

        packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
        packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
        packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
    }

    public static void castSecondSpell() {
        if(!canCastSpell()) return;

        NetHandlerPlayClient client = Minecraft.getMinecraft().getConnection();
        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
            packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
            packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
            return;
        }

        packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
        packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
        packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
    }

    public static void castThirdSpell() {
        if(!canCastSpell()) return;

        NetHandlerPlayClient client = Minecraft.getMinecraft().getConnection();
        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
            packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
            packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
            return;
        }

        packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
        packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
        packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
    }

    public static void castFourthSpell() {
        if(!canCastSpell()) return;

        NetHandlerPlayClient client = Minecraft.getMinecraft().getConnection();
        if(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER) {
            packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
            packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
            packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
            return;
        }

        packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
        packetQueue.add(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)); //right
        packetQueue.add(new CPacketAnimation(EnumHand.MAIN_HAND)); //left
    }

    //TODO find a way to make the time follow the player ping to avoid clicks not being triggered correctly
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if(packetQueue.size() == 0 || Minecraft.getMinecraft().world.getTotalWorldTime() % 5 != 0) return;

        Minecraft.getMinecraft().getConnection().sendPacket(packetQueue.get(0));
        packetQueue.remove(0);
    }

    private static boolean canCastSpell() {
        return Reference.onWorld && PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE;
    }

}
