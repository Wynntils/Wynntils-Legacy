/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.core.events.custom;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.fml.common.eventhandler.Event;


public class PacketEvent extends Event {

    public static class ResourcePackReceived extends Event {
        SPacketResourcePackSend packet;
        NetworkManager networkManager;

        public ResourcePackReceived(SPacketResourcePackSend packet, NetworkManager networkManager) {
            this.packet = packet;
            this.networkManager = networkManager;

        }

        public boolean isCancelable()
        {
            return true;
        }

        public SPacketResourcePackSend getPacket() {
            return packet;
        }

        public NetworkManager getNetworkManager() {
            return networkManager;
        }
    }



    public static class InventoryReceived extends Event {

        SPacketOpenWindow packet;
        NetHandlerPlayClient playClient;

        public InventoryReceived(SPacketOpenWindow packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable()
        {
            return true;
        }

        public SPacketOpenWindow getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }
    }

    public static class InventoryItemsReceived extends Event {

        SPacketWindowItems packet;
        NetHandlerPlayClient playClient;

        public InventoryItemsReceived(SPacketWindowItems packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable()
        {
            return true;
        }

        public SPacketWindowItems getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }
    }

    public static class SpawnObject extends Event {

        SPacketSpawnObject packet;
        NetHandlerPlayClient playClient;

        public SpawnObject(SPacketSpawnObject packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable()
        {
            return true;
        }

        public SPacketSpawnObject getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }

    }

}
