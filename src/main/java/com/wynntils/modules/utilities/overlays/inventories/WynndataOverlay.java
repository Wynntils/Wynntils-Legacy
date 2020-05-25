/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WynndataOverlay implements Listener {
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

    private String getItemNameFromInventory(NonNullList<ItemStack> inventory, int slot) {
        ItemStack stack = inventory.get(slot);
        return Utils.encodeItemNameForUrl(stack);
    }

    @SubscribeEvent
    public void mouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        e.getButtonList().forEach(gb -> {
            if (gb.id == 12 && gb.isMouseOver()) {
                if (e.getMouseButton() == 0) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));

                    NonNullList<ItemStack> armorInventory = Minecraft.getMinecraft().player.inventory.armorInventory;
                    String helmet = getItemNameFromInventory(armorInventory, 3);
                    String chestplate = getItemNameFromInventory(armorInventory, 2);
                    String leggings = getItemNameFromInventory(armorInventory, 1);
                    String boots = getItemNameFromInventory(armorInventory, 0);

                    NonNullList<ItemStack> mainInventory = Minecraft.getMinecraft().player.inventory.mainInventory;
                    String ring1 = getItemNameFromInventory(mainInventory, 9);
                    String ring2 = getItemNameFromInventory(mainInventory, 10);
                    String bracelet = getItemNameFromInventory(mainInventory, 11);
                    String amulet = getItemNameFromInventory(mainInventory, 12);
                    String weapon = getItemNameFromInventory(mainInventory, 0);

                    String url = String.format("https://www.wynndata.tk/builder?helmet=%s&chestplate=%s&leggings=%s&boots=%s&ring1=%s&ring2=%s&bracelet=%s&necklace=%s&weapon=%s&action=builder&weapon_powders=&helmet_powders=&chestplate_powders=&leggings_powders=&boots_powders=",
                            helmet, chestplate, leggings, boots, ring1, ring2, bracelet, amulet, weapon);
                    Utils.openUrl(url);
                }
            }
        });
    }
}
