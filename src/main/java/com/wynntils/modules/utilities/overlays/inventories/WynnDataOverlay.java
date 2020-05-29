/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.Powder;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WynnDataOverlay implements Listener {

    @SubscribeEvent
    public void initGui(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!Reference.onWorld) return;

        e.getButtonList().add(
                new GuiButton(12,
                        (e.getGui().width - e.getGui().getXSize()) / 2 - 20,
                        (e.getGui().height - e.getGui().getYSize()) / 2 + 40,
                        18, 18,
                        "➦"
                )
        );
    }

    @SubscribeEvent
    public void drawScreen(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        e.getButtonList().forEach(gb -> {
            if (gb.id == 12 && gb.isMouseOver()) {
                e.getGui().drawHoveringText("Open Build on WynnData", e.getMouseX(), e.getMouseY());
            }
        });
    }

    private void getItemNameFromInventory(Map<String, String> urlData, String typeName, NonNullList<ItemStack> inventory, int slot) {
        ItemStack stack = inventory.get(slot);
        if (!stack.isEmpty() && stack.getItem() != Item.getItemFromBlock(Blocks.SNOW_LAYER)) {
            urlData.put(typeName, Utils.encodeItemNameForUrl(stack));
        }
        ItemUtils.getLore(stack).forEach(line -> {
            if (line.contains("Powder Slots [")) {
                List<Powder> powders = Powder.findPowders(line);
                StringBuilder sb = new StringBuilder();
                powders.forEach(powder -> {
                    sb.append(powder.getLetterRepresentation());
                    sb.append("1");
                });
                urlData.put(typeName + "_powders", sb.toString());
                // Make sure user starts at the update powders screen
                urlData.put("action", "powders");
            }
        });
    }

    @SubscribeEvent
    public void mouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        e.getButtonList().forEach(gb -> {
            if (gb.id != 12 || !gb.isMouseOver() || e.getMouseButton() != 0) return;

            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));

            Map<String, String> urlData = new HashMap<>();

            NonNullList<ItemStack> armorInventory = Minecraft.getMinecraft().player.inventory.armorInventory;
            getItemNameFromInventory(urlData, "helmet", armorInventory, 3);
            getItemNameFromInventory(urlData, "chestplate", armorInventory, 2);
            getItemNameFromInventory(urlData, "leggings", armorInventory, 1);
            getItemNameFromInventory(urlData, "boots", armorInventory, 0);

            NonNullList<ItemStack> mainInventory = Minecraft.getMinecraft().player.inventory.mainInventory;
            getItemNameFromInventory(urlData, "ring1", mainInventory, 9);
            getItemNameFromInventory(urlData, "ring2", mainInventory, 10);
            getItemNameFromInventory(urlData, "bracelet", mainInventory, 11);
            getItemNameFromInventory(urlData, "necklace", mainInventory, 12);
            getItemNameFromInventory(urlData, "weapon", mainInventory, 0);

            StringBuilder urlBuilder = new StringBuilder("https://www.wynndata.tk/builder?");
            for (Map.Entry<String, String> itemName : urlData.entrySet()) {
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
