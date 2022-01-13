package com.wynntils.modules.utilities.managers;

import java.util.HashMap;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

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
        HashMap<Integer, Integer> emeraldPouches = new HashMap<Integer, Integer>() { // <inventory slot, usage>
        };

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && stack.hasDisplayName() && EmeraldPouchManager.isEmeraldPouch(stack)) {
                emeraldPouches.put(i, EmeraldPouchManager.getPouchUsage(stack));
            }
        }

        pouchSwitch:
        switch (emeraldPouches.size()) {
            default:
                boolean alreadyHasNonEmpty = false;
                Integer usedPouch = -1;
                for (Integer key : emeraldPouches.keySet()) {
                    if (emeraldPouches.get(key) > 0 && !alreadyHasNonEmpty) { // We've discovered one pouch with a non-zero balance, remember this
                        alreadyHasNonEmpty = true;
                        usedPouch = key; // Save our pouch slot ID
                    } else if (emeraldPouches.get(key) > 0) { // Another pouch has a non-zero balance; notify user
                        GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You have more than one filled emerald pouch in your inventory.");
                        break pouchSwitch;
                    }
                }

                // At this point, we have either multiple pouches with zero emeralds, or multiple pouches but only one with a non-zero balance
                // Check to make sure we don't have a bunch of zero balances - if we do, notify user
                if (!alreadyHasNonEmpty) {
                    GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You have more than one empty and no filled emerald pouches in your inventory.");
                    break;
                }

                // Now, we know we have 1 used pouch and 1+ empty pouches - just open the used one we saved from before
                player.connection.sendPacket(new CPacketClickWindow(
                        player.inventoryContainer.windowId,
                        usedPouch, 1, ClickType.PICKUP, player.inventory.getStackInSlot(usedPouch),
                        player.inventoryContainer.getNextTransactionID(player.inventory)));
                break;

            case 0:
                GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You do not have an emerald pouch in your inventory.");
                break;
            case 1:
                int slotNumber = emeraldPouches.entrySet().iterator().next().getKey(); // We can just get the first value in the HashMap since we only have one value
                if (slotNumber < 9) {
                    // sendPacket uses raw slot numbers, we need to remap the hotbar
                    slotNumber += 36;
                }
                player.connection.sendPacket(new CPacketClickWindow(
                        player.inventoryContainer.windowId,
                        slotNumber, 1, ClickType.PICKUP, player.inventory.getStackInSlot(slotNumber),
                        player.inventoryContainer.getNextTransactionID(player.inventory)));
                break;
        }
    }

}
