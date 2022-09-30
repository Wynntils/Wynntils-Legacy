package com.wynntils.modules.utilities.overlays.inventories;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.wynntils.McIf;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TradeMarketOverlay implements Listener {

    private final Pattern ITEM_NAME_PATTERN = Pattern.compile(".*Selling §f(\\d+|\\d+,\\d+) (.*)§6 for .* Each");

    private boolean shouldSend = false;
    private int amountToSend = 0;

    @SubscribeEvent
    public void onGuiUpdate(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if (!UtilitiesConfig.Market.INSTANCE.showSellAllButton) return;
        InventoryBasic inventory = (InventoryBasic) e.getGui().getLowerInv();
        if (!inventory.getName().startsWith("What would you like to sell?")) {
            shouldSend = false;
            return;
        }
        if (inventory.getStackInSlot(20) != ItemStack.EMPTY && !inventory.getStackInSlot(20).getDisplayName().isEmpty()) return;

        String itemName = getItemName(inventory);
        if (itemName == null) return;

        int amount = getAmount(itemName);

        addCustomSellItems(itemName, inventory);

        if (amount <= 1) return;

        ItemStack itemStack = new ItemStack(buildNBT(itemName, amount, 0));
        inventory.setInventorySlotContents(20, itemStack);
    }

    @SubscribeEvent
    public void onClick(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        InventoryBasic inventory = (InventoryBasic) e.getGui().getLowerInv();
        if (!inventory.getName().contains("What would you like to sell?")) return;

        ItemStack itemStack = e.getGui().getLowerInv().getStackInSlot(e.getSlotId());

        NBTTagCompound nbtTagCompound = itemStack.serializeNBT().getCompoundTag("tag");

        if (!nbtTagCompound.hasKey("wynntilsSellAmount")) return;

        amountToSend = nbtTagCompound.getInteger("wynntilsSellAmount");
        if (amountToSend <= 1) return;

        shouldSend = true;

        e.setCanceled(true);
        e.getGui().handleMouseClick(e.getGui().inventorySlots.getSlot(11), 11, 2, ClickType.PICKUP);
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent e) {
        if (!shouldSend) return;

        String message = ChatFormatting.stripFormatting(e.getMessage().getFormattedText());
        if (!message.trim().startsWith("Type the amount you wish to sell or type 'cancel' to cancel:")) return;

        McIf.player().sendChatMessage(String.valueOf(amountToSend));
        shouldSend = false;
    }

    private NBTTagCompound buildNBT(String itemName, int amount, int customSellAmountIndex) {
        boolean isCustomSellButton = customSellAmountIndex != 0;

        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound tag = nbt.getCompoundTag("tag");

        nbt.setString("id", "minecraft:diamond_axe");

        if (isCustomSellButton) nbt.setInteger("Count", customSellAmountIndex);
        else nbt.setInteger("Count", Math.min(amount, 64));

        NBTTagCompound display = tag.getCompoundTag("display");
        if(isCustomSellButton) display.setString("Name", "§aSell " + amount);
        else display.setString("Name", "§aSell All");

        NBTTagList lore = new NBTTagList();
        lore.appendTag(new NBTTagString("§7This will sell: §6" + amount + " §7of " + itemName));
        display.setTag("Lore", lore);

        tag.setInteger("Unbreakable", 1);
        tag.setInteger("HideFlags", 255);

        if (isCustomSellButton) nbt.setShort("Damage", (short) (96 + customSellAmountIndex));
        else nbt.setShort("Damage", (short) (95));

        tag.setTag("display", display);
        tag.setInteger("wynntilsSellAmount", amount);
        nbt.setTag("tag", tag);

        return nbt;
    }

    private int getAmount(String name) {
        int amount = 0;

        for (ItemStack item : McIf.player().inventory.mainInventory) {
            if (item.getDisplayName().toLowerCase().trim().equals(name.toLowerCase().trim())) {
                int itemAmount = item.getCount();
                amount += itemAmount;
            }
        }

        return amount;
    }

    private String getItemName(InventoryBasic inventory) {
        ItemStack itemStack = inventory.getStackInSlot(10);
        if (itemStack == ItemStack.EMPTY) return null;
        if (itemStack.getDisplayName().contains("Click an Item to sell")) return null;

        Matcher matcher = ITEM_NAME_PATTERN.matcher(itemStack.getDisplayName());

        if (!matcher.matches()) return null;
        return matcher.group(2);
    }

    private void addCustomSellItems(String itemName, InventoryBasic inventory) {

        int firstAmount = StringUtils.parseIntOr(UtilitiesConfig.Market.INSTANCE.customSellButton1, 0);
        if (firstAmount > 0) {
            ItemStack itemStack = new ItemStack(buildNBT(itemName, firstAmount, 1));
            inventory.setInventorySlotContents(1, itemStack);
        }

        int secondAmount = StringUtils.parseIntOr(UtilitiesConfig.Market.INSTANCE.customSellButton2, 0);
        if (secondAmount > 0) {
            ItemStack itemStack = new ItemStack(buildNBT(itemName, secondAmount, 2));
            inventory.setInventorySlotContents(2, itemStack);
        }

        int thirdAmount = StringUtils.parseIntOr(UtilitiesConfig.Market.INSTANCE.customSellButton3, 0);
        if (thirdAmount > 0) {
            ItemStack itemStack = new ItemStack(buildNBT(itemName, thirdAmount, 3));
            inventory.setInventorySlotContents(3, itemStack);
        }
    }

}
