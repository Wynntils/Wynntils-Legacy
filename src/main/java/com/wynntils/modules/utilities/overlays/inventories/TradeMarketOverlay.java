package com.wynntils.modules.utilities.overlays.inventories;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.wynntils.McIf;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TradeMarketOverlay implements Listener {

    private final Pattern ITEM_NAME_PATTERN = Pattern.compile(".*Selling §f\\d+ (.*)§6 for .* Each");
    private boolean shouldSend = false;
    private int amountToSend = 0;

    @SubscribeEvent
    public void onGuiUpdate(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        InventoryBasic inventory = (InventoryBasic) e.getGui().getLowerInv();
        if (!inventory.getName().startsWith("What would you like to sell?")) {
            shouldSend = false;
            return;
        }
        if (inventory.getStackInSlot(20) != ItemStack.EMPTY && !inventory.getStackInSlot(20).getDisplayName().isEmpty()) return;

        ItemStack itemStack = new ItemStack(buildNBT(inventory));
        itemStack.setTagCompound(buildNBT(inventory));

        inventory.setInventorySlotContents(20, itemStack);
        shouldSend = true;
    }

    @SubscribeEvent
    public void onClick(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        InventoryBasic inventory = (InventoryBasic) e.getGui().getLowerInv();
        if (!inventory.getName().contains("What would you like to sell?")) return;
        if (e.getSlotId() != 20) return;

        String itemName = getItemName(inventory);
        if(itemName == null) return;

        int slotId = 11;
        shouldSend = true;
        amountToSend = getAmount(itemName);

        e.getGui().handleMouseClick(e.getGui().inventorySlots.getSlot(slotId), slotId, 2, ClickType.PICKUP);
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent e) {
        if (!shouldSend) return;

        String message = ChatFormatting.stripFormatting(e.getMessage().getFormattedText());
        if (!message.trim().startsWith("Type the amount you wish to sell or type 'cancel' to cancel:")) return;

        McIf.player().sendChatMessage(String.valueOf(amountToSend));
        shouldSend = false;
    }

    public NBTTagCompound buildNBT(InventoryBasic inventory) {
        String itemName = getItemName(inventory);

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();

        nbt.setString("id", "minecraft:emerald");
        nbt.setInteger("Count", 1);

        NBTTagCompound display = new NBTTagCompound();
        display.setString("Name", "§aSell All");

        NBTTagList lore = new NBTTagList();
        lore.appendTag(new NBTTagString("§7Click to sell all items in your inventory"));

        if(itemName != null)
            lore.appendTag(new NBTTagString("§7You have: §6" + getAmount(itemName) + " §7of " + itemName));
        else lore.appendTag(new NBTTagString("§cYou have not yet selected an item to sell!"));

        display.setTag("Lore", lore);

        tag.setFloat("Unbreakable", 1);
        tag.setInteger("HideFlags", 6);

        nbt.setTag("display", display);
        nbt.setTag("tag", tag);
        nbt.setFloat("Damage", 0);

        return nbt;
    }

    public int getAmount(String name) {
        int amount = 0;

        for (ItemStack item : McIf.player().inventory.mainInventory) {
            if (item.getDisplayName().toLowerCase().trim().equals(name.toLowerCase().trim())) {
                int itemAmount = item.getCount();
                amount += itemAmount;
            }
        }

        return amount;
    }

    public String getItemName(InventoryBasic inventory) {
        ItemStack itemStack = inventory.getStackInSlot(10);
        if (itemStack == ItemStack.EMPTY) return null;
        if (itemStack.getDisplayName().contains("Click an Item to sell")) return null;

        Matcher matcher = ITEM_NAME_PATTERN.matcher(itemStack.getDisplayName());

        if (!matcher.matches()) return null;
        return matcher.group(1);
    }

}
