package com.wynntils.modules.utilities.overlays.inventories;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.wynntils.McIf;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TradeMarketOverlay implements Listener {

    private final Pattern ITEM_NAME_PATTERN = Pattern.compile(".*Selling §f\\d+ (.*) for .* Each");

    @SubscribeEvent
    public void onRender(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        InventoryBasic inventory = (InventoryBasic) e.getGui().getLowerInv();

        if (!inventory.getName().contains("What would you like to sell?")) return;
        if(e.getSlotId() != 11) return;

        ItemStack itemStack = inventory.getStackInSlot(10);

        if (itemStack == ItemStack.EMPTY) return;
        if (itemStack.getDisplayName().contains("Click an Item to Sell")) return;

        Matcher matcher = ITEM_NAME_PATTERN.matcher(itemStack.getDisplayName());

        if (!matcher.matches()) return;

        String itemName = matcher.group(1);
        int amount = 0;

        for (ItemStack item : McIf.player().inventory.mainInventory) {
            if (ChatFormatting.stripFormatting(item.getDisplayName()).equals(ChatFormatting.stripFormatting(itemName))) {
                int itemAmount = item.getCount();
                amount += itemAmount;
            }
        }

        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(ChatFormatting.DARK_GRAY + "You have " + ChatFormatting.GOLD + ChatFormatting.BOLD + amount + " " + itemName + ChatFormatting.DARK_GRAY + " in your inventory"));
    }
}
