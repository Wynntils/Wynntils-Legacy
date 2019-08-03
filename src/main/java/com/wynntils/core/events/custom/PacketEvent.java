/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.Event;


/**
 * Represents events that are triggered when a packet is sent to the client
 * all of them are cancelable (you can avoid it to reach the client processor)
 *
 */
public class PacketEvent extends Event {

    /**
     * Triggered when the client receives a {@link ResourcePackReceived} packet from the server
     */
    public static class ResourcePackReceived extends PacketEvent {
        SPacketResourcePackSend packet;
        NetHandlerPlayClient playClient;

        public ResourcePackReceived(SPacketResourcePackSend packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable()
        {
            return true;
        }

        public SPacketResourcePackSend getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }
    }

    /**
     * Triggered when the client receives a {@link SPacketOpenWindow} packet from the server
     */
    public static class InventoryReceived extends PacketEvent {

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

    /**
     * Triggered when the client receives a {@link SPacketWindowItems} packet from the server
     */
    public static class InventoryItemsReceived extends PacketEvent {

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

    /**
     * Triggered when the client receives a {@link SPacketSpawnObject} packet from the server
     */
    public static class SpawnObject extends PacketEvent {

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

    /**
     * Triggered when the client receives a {@link SPacketPlayerListItem} packet from the server
     */
    public static class TabListChangeEvent extends PacketEvent {
        SPacketPlayerListItem packet;
        NetHandlerPlayClient playClient;

        public TabListChangeEvent(SPacketPlayerListItem packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable() {
            return true;
        }

        public SPacketPlayerListItem getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }
    }
    
    /**
     * Triggered when the client receives a {@link SPacketTitle} packet from the server
     */
    public static class TitleEvent extends Event {
        SPacketTitle packet;
        NetHandlerPlayClient playClient;

        public TitleEvent(SPacketTitle packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;

        }

        public boolean isCancelable() {
            return true;
        }

        public SPacketTitle getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }
    }

    /**
     * Triggered when the player tries to drop an item and {@link CPacketPlayerDigging} is triggered
     */
    public static class PlayerDropItemEvent extends PacketEvent {

        CPacketPlayerDigging packet;
        NetHandlerPlayClient playClient;

        public PlayerDropItemEvent(CPacketPlayerDigging packet, NetHandlerPlayClient playClient) {
            this.packet = packet; this.playClient = playClient;
        }

        public CPacketPlayerDigging getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }

        public boolean isCancelable() {
            return true;
        }

    }

    /**
     * Triggered when the player try to use an item
     */
    public static class PlayerUseItemEvent extends PacketEvent {

        CPacketPlayerTryUseItem packet;
        NetHandlerPlayClient playClient;

        public PlayerUseItemEvent(CPacketPlayerTryUseItem packet, NetHandlerPlayClient playClient) {
            this.packet = packet; this.playClient = playClient;
        }

        public CPacketPlayerTryUseItem getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }

        public boolean isCancelable() {
            return true;
        }

    }

    /**
     * Triggered when the player try to use an item on a block
     */
    public static class PlayerUseItemOnBlockEvent extends PacketEvent {

        CPacketPlayerTryUseItemOnBlock packet;
        NetHandlerPlayClient playClient;

        public PlayerUseItemOnBlockEvent(CPacketPlayerTryUseItemOnBlock packet, NetHandlerPlayClient playClient) {
            this.packet = packet; this.playClient = playClient;
        }

        public CPacketPlayerTryUseItemOnBlock getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }

        public boolean isCancelable() {
            return true;
        }

    }

    /**
     * Triggered when the players right clicks an entity
     */
    public static class UseEntityEvent extends PacketEvent {

        CPacketUseEntity packet;
        NetHandlerPlayClient playClient;

        public UseEntityEvent(CPacketUseEntity packet, NetHandlerPlayClient playClient) {
            this.packet = packet; this.playClient = playClient;
        }

        public CPacketUseEntity getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }

        public boolean isCancelable() {
            return true;
        }

    }

    /**
     * Triggered when a {@link SPacketEntityMetadata} is sent to the client
     */
    public static class EntityMetadata extends PacketEvent {

        SPacketEntityMetadata packet;
        NetHandlerPlayClient playClient;

        public EntityMetadata(SPacketEntityMetadata packet, NetHandlerPlayClient playClient) {
            this.packet = packet; this.playClient = playClient;
        }

        public SPacketEntityMetadata getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }

        public boolean isCancelable() {
            return true;
        }

    }

    /**
     * Triggered when a {@link SPacketSetExperience} is sent to the client
     */
    public static class SetExperience extends PacketEvent {

        SPacketSetExperience packet;
        NetHandlerPlayClient playClient;

        public SetExperience(SPacketSetExperience packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;
        }

        public SPacketSetExperience getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }

        public boolean isCancelable() {
            return true;
        }

    }

    /**
     * Triggered when a {@link SPacketSpawnPosition} is sent to the client
     */
    public static class SpawnPosition extends PacketEvent {

        SPacketSpawnPosition packet;
        NetHandlerPlayClient playClient;

        public SpawnPosition(SPacketSpawnPosition packet, NetHandlerPlayClient playClient) {
            this.packet = packet;
            this.playClient = playClient;
        }

        public SPacketSpawnPosition getPacket() {
            return packet;
        }

        public NetHandlerPlayClient getPlayClient() {
            return playClient;
        }

        public boolean isCancelable() {
            return true;
        }

    }
}
