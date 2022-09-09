/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.Powder;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WynnBuilderOverlay implements Listener {

    // WynnBuilder uses their own Base64 converter
    private static final String DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+-";
    private static final Pattern SKILLPOINT_PATTERN = Pattern.compile(".*?([-0-9]+)(?=\\spoints).*");

    @SubscribeEvent
    public void initGui(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!Reference.onWorld || !Utils.isCharacterInfoPage(e.getGui())) return;

        e.getButtonList().add(
                new GuiButton(12,
                        (e.getGui().width - e.getGui().getXSize()) / 2 - 20,
                        (e.getGui().height - e.getGui().getYSize()) / 2 + 100,
                        18, 18,
                        "➦"
                )
        );
    }

    @SubscribeEvent
    public void drawScreen(GuiOverlapEvent.ChestOverlap.DrawScreen.Post e) {
        e.getButtonList().forEach(gb -> {
            if (gb.id == 12 && gb.isMouseOver()) {
                e.getGui().drawHoveringText(Arrays.asList("Left click: Open Build on WynnBuilder", "Middle-click on item: Open Item on WynnBuilder"), e.getMouseX(), e.getMouseY());
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void clickOnChest(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (!Utils.isCharacterInfoPage(e.getGui()) || e.getMouseButton() != 2) return;

        Slot slot = e.getGui().getSlotUnderMouse();
        if (slot == null || slot.inventory == null || !slot.getHasStack()) return;

        ItemStack stack = slot.getStack();
        if (ItemUtils.getStringLore(stack).contains("§3Crafted")) return; // Crafteds are not on wynnbuilder

        Utils.openUrl("https://wynnbuilder.github.io/item.html#" + Utils.getRawItemName(stack).replace(" ", "%20"));
        e.setCanceled(true);
    }

    private void getItemIDFromInventory(StringBuilder urlData, int notSet, NonNullList<ItemStack> inventory, int slot) {
        ItemStack stack = inventory.get(slot);
        if (!stack.isEmpty() && WebManager.getItems().containsKey(Utils.getRawItemName(stack))) {
            urlData.append(fromIntN(WebManager.getItems().get(Utils.getRawItemName(stack)).getWynnBuilderID(), 3));
        } else {
            urlData.append(fromIntN(notSet, 3));
        }
    }

    private void getPowders(StringBuilder urlData, NonNullList<ItemStack> inventory, int slot) {
        ItemStack stack = inventory.get(slot);
        if (ItemUtils.getStringLore(stack).contains("Powder Slots [")) {
            ItemUtils.getLore(stack).forEach(line -> {
                if (line.contains("Powder Slots [")) {
                    List<Powder> powders = Powder.findPowders(line);
                    int powderSections = (int) Math.ceil((double) powders.size() / 6.0);

                    // Number of powders / 6 for looping
                    urlData.append(fromIntN(powderSections, 1));

                    while (!powders.isEmpty()) {
                        List<Powder> section = powders.subList(0, Math.min(6, powders.size()));
                        int powderHash = 0;
                        for (Powder powder : section) {
                            // Powders each use 5 bits
                            powderHash <<= 5;
                            // Powder ids are the ordinal of the type of powder * 6 + tier of the powder - 1 and adds 1 to each powder
                            powderHash += 6 + powder.ordinal() * 6;
                        }
                        urlData.append(fromIntN(powderHash, 5));
                        powders = new ArrayList<>(powders.subList(Math.min(6, powders.size()), powders.size()));
                    }
                }
            });
        } else {
            //No powders
            urlData.append(fromIntN(0, 1));
        }
    }

    private void getSkillpoint(StringBuilder urlData, NonNullList<ItemStack> inventory, int slot) {
        String lore = ItemUtils.getStringLore(inventory.get(slot));
        Matcher spm = SKILLPOINT_PATTERN.matcher(lore);
        if (!spm.find()) return;

        int value = Integer.parseInt(spm.group(1));
        urlData.append(fromIntN(value, 2));
    }

    private int locateWeaponSlot() {
        for (int i = 0; i < 9; i++) {
            String lore = ItemUtils.getStringLore(McIf.player().inventory.mainInventory.get(i));
            // Assume that only weapons have class requirements
            if (lore.contains("Class Req: " + PlayerInfo.get(CharacterData.class).getCurrentClass().getDisplayName())) {
                return i;
            }
        }
        return -1;
    }

    // Basically just a java version of the base64 method WynnBuilder uses
    private String fromIntN(int num, int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(DIGITS.charAt(num & 0x3f));
            num >>= 6;
        }
        result.reverse();
        return result.toString();
    }

    @SubscribeEvent
    public void mouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        e.getButtonList().forEach(gb -> {
            if (gb.id != 12 || !gb.isMouseOver() || e.getMouseButton() != 0) return;

            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));

            // WynnBuilder uses base64 for builds
            StringBuilder urlData = new StringBuilder("https://wynnbuilder.github.io/#4_");

            // First are the ids of the items as well as details of crafted items
            int notSet = 10000;
            NonNullList<ItemStack> armorInventory = McIf.player().inventory.armorInventory;
            getItemIDFromInventory(urlData, notSet++, armorInventory, 3);
            getItemIDFromInventory(urlData, notSet++, armorInventory, 2);
            getItemIDFromInventory(urlData, notSet++, armorInventory, 1);
            getItemIDFromInventory(urlData, notSet++, armorInventory, 0);

            NonNullList<ItemStack> mainInventory = McIf.player().inventory.mainInventory;
            getItemIDFromInventory(urlData, notSet++, mainInventory, 9);
            getItemIDFromInventory(urlData, notSet++, mainInventory, 10);
            getItemIDFromInventory(urlData, notSet++, mainInventory, 11);
            getItemIDFromInventory(urlData, notSet++, mainInventory, 12);

            int weaponSlot = locateWeaponSlot();
            if (weaponSlot != -1) {
                getItemIDFromInventory(urlData, notSet++, mainInventory, weaponSlot);
            } else {
                urlData.append(fromIntN(notSet++, 3));
            }

            // Second are the skillpoint allocations
            for (int i = 9; i < 9 + 5; i++) {
                getSkillpoint(urlData, e.getGui().inventorySlots.getInventory(), i);
            }

            // Third is the players level
            urlData.append(fromIntN(PlayerInfo.get(CharacterData.class).getLevel(), 2));

            // Fourth is the powders
            getPowders(urlData, armorInventory, 3);
            getPowders(urlData, armorInventory, 2);
            getPowders(urlData, armorInventory, 1);
            getPowders(urlData, armorInventory, 0);

            if (weaponSlot != -1) {
                getPowders(urlData, mainInventory, weaponSlot);
            } else {
                // No powders
                urlData.append(fromIntN(0, 1));
            }
//            for (Map.Entry<String, String> itemName : urlData.entrySet()) {
//                urlBuilder
//                        .append(itemName.getKey())
//                        .append('=')
//                        .append(itemName.getValue())
//                        .append('&');
//            }
//            urlBuilder.replace(urlBuilder.length() - 1, urlBuilder.length(), "");

            Utils.openUrl(urlData.toString());
        });
    }

}
