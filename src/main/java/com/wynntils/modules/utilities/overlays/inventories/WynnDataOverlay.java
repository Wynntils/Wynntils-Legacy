/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WynnDataOverlay implements Listener {
    GuiButton button;
    public static boolean itemLookupMode = false;

    @SubscribeEvent
    public void initGui(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!Reference.onWorld || !Utils.isCharacterInfoPage(e.getGui())) return;

        button = new GuiButton(12,
                        (e.getGui().width - e.getGui().getXSize()) / 2 - 20,
                        (e.getGui().height - e.getGui().getYSize()) / 2 + 40,
                        18, 18,
                        "➦"
                );
        e.getButtonList().add(button);
    }

    @SubscribeEvent
    public void drawScreen(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        e.getButtonList().forEach(gb -> {
            if (gb.id == 12 && gb.isMouseOver()) {
                if (itemLookupMode) {
                    e.getGui().drawHoveringText( Arrays.asList("Right click on item", "to open on WynnData"),  e.getMouseX(), e.getMouseY());
                } else {
                    e.getGui().drawHoveringText(Arrays.asList("Left click: Open Build on WynnData", "Right click: Toggle item lookup mode"), e.getMouseX(), e.getMouseY());
                }
            }
        });
    }

    @SubscribeEvent
    public void characterInfoPageOpened(GuiScreenEvent.InitGuiEvent.Post e) {
        if (Utils.isCharacterInfoPage(e.getGui())) {
            // Reset lookup mode when re-opening character info page
            itemLookupMode = false;
        }
    }

    @SubscribeEvent
    public void clickOnChest(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (!Utils.isCharacterInfoPage(e.getGui()) || !WynnDataOverlay.itemLookupMode ||
                e.getMouseButton() != 1 || e.getGui().getSlotUnderMouse() == null ||
                e.getGui().getSlotUnderMouse().inventory == null) return;

        ItemStack stack = e.getGui().getSlotUnderMouse().getStack();
        Utils.openUrl("https://www.wynndata.tk/i/" + Utils.encodeItemNameForUrl(stack));
        e.setCanceled(true);
    }

    private void getItemNameFromInventory(Map<String, String> itemNames, String typeName, NonNullList<ItemStack> inventory, int slot) {
        ItemStack stack = inventory.get(slot);
        if (!stack.isEmpty() && stack.getItem() != Item.getItemFromBlock(Blocks.SNOW_LAYER)) {
            itemNames.put(typeName, Utils.encodeItemNameForUrl(stack));
        }
    }

    @SubscribeEvent
    public void mouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        e.getButtonList().forEach(gb -> {
            if (gb.id != 12 || !gb.isMouseOver()) return;

            if (e.getMouseButton() == 1) {
                // Toggle item lookup mode with right click
                itemLookupMode = !itemLookupMode;
                button.packedFGColour = itemLookupMode ? CommonColors.ORANGE.toInt() : 0;
                return;
            }

            if (e.getMouseButton() != 0) return;

            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));

            Map<String, String> itemNames = new HashMap<>();
            
            NonNullList<ItemStack> armorInventory = Minecraft.getMinecraft().player.inventory.armorInventory;
            getItemNameFromInventory(itemNames, "helmet", armorInventory, 3);
            getItemNameFromInventory(itemNames, "chestplate", armorInventory, 2);
            getItemNameFromInventory(itemNames, "leggings", armorInventory, 1);
            getItemNameFromInventory(itemNames, "boots", armorInventory, 0);

            NonNullList<ItemStack> mainInventory = Minecraft.getMinecraft().player.inventory.mainInventory;
            getItemNameFromInventory(itemNames, "ring1", mainInventory, 9);
            getItemNameFromInventory(itemNames, "ring2", mainInventory, 10);
            getItemNameFromInventory(itemNames, "bracelet", mainInventory, 11);
            getItemNameFromInventory(itemNames, "necklace", mainInventory, 12);
            getItemNameFromInventory(itemNames, "weapon", mainInventory, 0);

            StringBuilder urlBuilder = new StringBuilder("https://www.wynndata.tk/builder?");
            for (Map.Entry<String, String> itemName : itemNames.entrySet()) {
                urlBuilder
                        .append(itemName.getKey())
                        .append('=')
                        .append(itemName.getValue())
                        .append('&');
            }
            urlBuilder.replace(urlBuilder.length() - 1, urlBuilder.length(), "");

            Utils.openUrl(urlBuilder.toString());
        });
    }
    
}
