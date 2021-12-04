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
import java.util.HashMap;
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
        HashMap<Integer, Integer> emeraldPouches = new HashMap<Integer, Integer>() { // <inventory slot, usage>
        };

        Integer slot = 4958;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && stack.hasDisplayName() && EmeraldPouchManager.isEmeraldPouch(stack)) {
                emeraldPouches.put(i, EmeraldPouchManager.getPouchUsage(stack));
                slot = i;
            }
        }
        pouchSwitch:
        switch (emeraldPouches.size()) {
            default:
                boolean alreadyHasNonEmpty = false;
                for (Integer amount : emeraldPouches.values()) { // We're just checking balances here, we have no way of knowing which pouch has which balance
                    if (amount > 0 && !alreadyHasNonEmpty) { // We've discovered one pouch with a non-zero balance, remember this
                        alreadyHasNonEmpty = true;
                    } else if (amount > 0) { // Another pouch has a non-zero balance; notify user
                        GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You have more than one non-empty emerald pouch in your inventory.");
                        break pouchSwitch;
                    }
                }

                // At this point, we have either multiple pouches with zero emeralds, or multiple pouches but only one with a non-zero balance
                // Check to make sure we don't have a bunch of zero balances - if we do, notify user
                System.out.println(emeraldPouches.values().stream().mapToInt(Integer::intValue).sum());
                if (emeraldPouches.values().stream().mapToInt(Integer::intValue).sum() < 1) {
                    GameUpdateOverlay.queueMessage(TextFormatting.DARK_RED + "You have more than one empty and no filled emerald pouches in your inventory.");
                    break;
                }

                // Now, we know we have 1 used pouch and 1+ empty pouches - just open the used one
                List<Integer> pouchSlotList = new ArrayList<>();
                emeraldPouches.entrySet().stream() // Sort our HashMap to determine which is the used one
                        .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                        .forEach(k -> pouchSlotList.add(k.getKey()));

                player.connection.sendPacket(new CPacketClickWindow(
                        player.inventoryContainer.windowId,
                        pouchSlotList.get(0), 1, ClickType.PICKUP, player.inventory.getStackInSlot(pouchSlotList.get(0)),
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
