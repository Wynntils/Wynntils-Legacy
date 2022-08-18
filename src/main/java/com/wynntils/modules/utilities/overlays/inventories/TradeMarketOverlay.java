package com.wynntils.modules.utilities.overlays.inventories;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.wynntils.McIf;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TradeMarketOverlay implements Listener {

    private final Pattern ITEM_NAME_PATTERN = Pattern.compile("Selling \\d+ (.*) for .* Each");

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent e) {
        if (!OverlayConfig.TradeMarket.INSTANCE.tradeMarketItemAmount) return;
        if (McIf.player() == null) return;
        if (McIf.player().openContainer == null) return;
        Container openContainer = McIf.player().openContainer;
        if (!(openContainer instanceof ContainerChest)) return;
        ContainerChest containerChest = (ContainerChest) openContainer;
        InventoryBasic inventory = (InventoryBasic) containerChest.getLowerChestInventory();
        if (!inventory.getName().contains("What would you like to sell?")) return;
        ItemStack itemStack = inventory.getStackInSlot(10);
        if (itemStack == ItemStack.EMPTY) return;
        if (itemStack.getDisplayName().contains("Click an Item to Sell")) return;
        Matcher matcher = ITEM_NAME_PATTERN.matcher(ChatFormatting.stripFormatting(itemStack.getDisplayName()));
        if (!matcher.matches()) return;
        String itemName = matcher.group(1);

        int amount = 0;
        for (ItemStack item : McIf.player().inventory.mainInventory) {
            if (ChatFormatting.stripFormatting(item.getDisplayName()).equals(itemName)) {
                int itemAmount = item.getCount();
                amount += itemAmount;
            }
        }
        String text = "You have " + amount + " " + itemName + " in your inventory";

        assert McIf.mc().currentScreen != null;
        int width = (McIf.mc().currentScreen.width/2) - McIf.mc().fontRenderer.getStringWidth(text)/2;
        int height = McIf.mc().currentScreen.height/2-100;
        McIf.mc().fontRenderer.drawString(text, width, height, 0xFFFFFF);
    }
}
