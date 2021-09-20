/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.items.events;

import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.items.managers.ItemStackTransformManager;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {
    @SubscribeEvent
    public void onSPacketEntityEquipment(PacketEvent<SPacketEntityEquipment> e) {
        ItemStack item = ReflectionFields.SPacketEntityEquipment_itemStack.getValue(e.getPacket());
        ReflectionFields.SPacketEntityEquipment_itemStack.setValue(e.getPacket(), ItemStackTransformManager.entityTransform(item, e.getPacket().getEntityID()));
    }

    @SubscribeEvent
    public void onSPacketWindowItems(PacketEvent<SPacketWindowItems> e) {
        ItemStack item = ReflectionFields.SPacketWindowItems_itemStacks.getValue(e.getPacket());
        ReflectionFields.SPacketWindowItems_itemStacks.setValue(e.getPacket(), ItemStackTransformManager.guiTransform(item, e.getPacket().getWindowId()));
    }

    @SubscribeEvent
    public void onSPacketSetSlot(PacketEvent<SPacketSetSlot> e) {
        ItemStack item = ReflectionFields.SPacketSetSlot_item.getValue(e.getPacket());
        ReflectionFields.SPacketSetSlot_item.setValue(e.getPacket(), ItemStackTransformManager.guiTransform(item, e.getPacket().getWindowId()));
    }
}
