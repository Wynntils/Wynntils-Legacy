/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.instances.inventory;

import com.wynntils.modules.core.interfaces.IInventoryOpenAction;
import com.wynntils.modules.core.managers.PacketQueue;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketOpenWindow;

public class InventoryOpenByCommand implements IInventoryOpenAction {

    String inputCommand;

    public InventoryOpenByCommand(String inputCommand) {
        this.inputCommand = inputCommand;
    }

    @Override
    public void onOpen(FakeInventory inv, Runnable onDrop) {
        PacketQueue.queueComplexPacket(new CPacketChatMessage(inputCommand), SPacketOpenWindow.class).onDrop(onDrop);
    }

}
