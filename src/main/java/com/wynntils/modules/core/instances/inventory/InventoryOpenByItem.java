package com.wynntils.modules.core.instances.inventory;

import com.wynntils.ModCore;
import com.wynntils.modules.core.interfaces.IInventoryOpenAction;
import com.wynntils.modules.core.managers.PacketQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.EnumHand;

public class InventoryOpenByItem implements IInventoryOpenAction {

    private static final CPacketPlayerTryUseItem rightClick = new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND);
    public static final Packet<?> ignoredPacket = rightClick;

    int inputSlot;

    public InventoryOpenByItem(int inputSlot) {
        this.inputSlot = inputSlot;
    }

    @Override
    public void onOpen(FakeInventory inv, Runnable onDrop) {
        Minecraft mc = ModCore.mc();

        PacketQueue.queueComplexPacket(rightClick, SPacketOpenWindow.class).setSender((conn, pack) -> {
            if (mc.player.inventory.currentItem != inputSlot) {
                conn.sendPacket(new CPacketHeldItemChange(inputSlot));
            }

            conn.sendPacket(pack);
            if (mc.player.inventory.currentItem != inputSlot) {
                conn.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            }
        }).onDrop(onDrop);
    }

}
