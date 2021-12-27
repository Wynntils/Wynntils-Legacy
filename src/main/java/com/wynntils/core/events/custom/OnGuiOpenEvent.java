package com.wynntils.core.events.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.wynntils.core.utils.helpers.Delay;

public class OnGuiOpenEvent {
    public static boolean CompassOpened = false;

    @SubscribeEvent
    public void onGuiOpen(net.minecraftforge.client.event.GuiOpenEvent event) {
        if(event.getGui() != null && event.getGui() instanceof GuiContainer && CompassOpened) {
            CompassOpened = false;
            new Delay(() -> {
                GuiContainer container = (GuiContainer) event.getGui();

                Minecraft minecraft = Minecraft.getMinecraft();

                CPacketClickWindow packet = new CPacketClickWindow(container.inventorySlots.windowId, 37, 0,
                        ClickType.PICKUP, container.inventorySlots.getSlot(37).getStack(),
                        container.inventorySlots.getNextTransactionID(minecraft.player.inventory));

                NetHandlerPlayClient connection = minecraft.getConnection();
                if (connection != null)
                    connection.sendPacket(packet);
            }, 1);
        }
    }
}
