package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class PouchHotkeyManager {

    public static void onIngredientHotkeyPress() {
        if (!Reference.onWorld) return;

        EntityPlayerSP player = McIf.player();
        player.connection.sendPacket(new CPacketClickWindow(
                player.inventoryContainer.windowId,
                13, 0, ClickType.PICKUP, player.inventory.getStackInSlot(13),
                player.inventoryContainer.getNextTransactionID(player.inventory)
        ));
    }

    public static void onEmeraldHotkeyPress() {
        if (!Reference.onWorld) return;

        EntityPlayerSP player = McIf.player();
        NonNullList<ItemStack> inventory = player.inventory.mainInventory;
        List<Integer> emeraldPouchSlots = new ArrayList<Integer>() {};

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && stack.hasDisplayName() && stack.getDisplayName().contains("§aEmerald Pouch§2 [Tier")) { // Match as much as possible of the emerald pouch name to prevent false positives
                emeraldPouchSlots.add(i);
            }
        }
        switch (emeraldPouchSlots.size()) {
            default:
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You have more than one emerald pouch in your inventory.");
            case 0:
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You do not have an emerald pouch in your inventory.");
            case 1:
                int slotNumber = emeraldPouchSlots.get(0);
                if (slotNumber < 9) {
                    // sendPacket uses raw slot numbers, we need to remap the hotbar
                    slotNumber += 36;
                }
                player.connection.sendPacket(new CPacketClickWindow(
                        player.inventoryContainer.windowId,
                        slotNumber, 1, ClickType.PICKUP, player.inventory.getStackInSlot(slotNumber),
                        player.inventoryContainer.getNextTransactionID(player.inventory)));
        }
    }

}
