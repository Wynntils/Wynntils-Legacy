/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WynnDataOverlay implements Listener {

    @SubscribeEvent
    public void initGui(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!Reference.onWorld || !Utils.isCharacterInfoPage(e.getGui())) return;

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
                e.getGui().drawHoveringText(Arrays.asList("Left click: Open Build on WynnData", "Shift + Right click on item: Open Item on WynnData"), e.getMouseX(), e.getMouseY());
            }
        });
    }

    @SubscribeEvent
    public void clickOnChest(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (!Utils.isCharacterInfoPage(e.getGui()) || e.getMouseButton() != 1) return;

        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) return;

        Slot slot = e.getGui().getSlotUnderMouse();
        if (slot == null || slot.inventory == null || !slot.getHasStack()) return;

        ItemStack stack = slot.getStack();
        Utils.openUrl("https://www.wynndata.tk/i/" + Utils.encodeItemNameForUrl(stack));
        e.setCanceled(true);
    }

    private void getItemNameFromInventory(Map<String, String> itemNames, String typeName, NonNullList<ItemStack> inventory, int slot) {
        ItemStack stack = inventory.get(slot);
        if (!stack.isEmpty() && WebManager.getItems().containsKey(Utils.getRawItemName(stack))) {
            itemNames.put(typeName, Utils.encodeItemNameForUrl(stack));
        }
    }

    private int locateWeaponSlot() {
        for (int i = 0; i < 9; i++) {
            String lore = ItemUtils.getStringLore(Minecraft.getMinecraft().player.inventory.mainInventory.get(i));
            // Assume that only weapons have class requirements
            if (lore.contains("Class Req: " + PlayerInfo.getPlayerInfo().getCurrentClass().getDisplayName())) {
                return i;
            }
        }
        return -1;
    }

    @SubscribeEvent
    public void mouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        e.getButtonList().forEach(gb -> {
            if (gb.id != 12 || !gb.isMouseOver() || e.getMouseButton() != 0) return;

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

            int weaponSlot = locateWeaponSlot();
            if (weaponSlot != -1) {
                getItemNameFromInventory(itemNames, "weapon", mainInventory, weaponSlot);
            }

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
