/**
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class LoreChangerOverlay implements Listener {

    @SubscribeEvent
    public void onChest(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if (e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;

        replaceLore(e.getGui().getSlotUnderMouse().getStack());
    }

    @SubscribeEvent
    public void onInventory(GuiOverlapEvent.InventoryOverlap.DrawScreen e) {
        if (e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;

        replaceLore(e.getGui().getSlotUnderMouse().getStack());
    }

    @SubscribeEvent
    public void onHorse(GuiOverlapEvent.HorseOverlap.DrawScreen e) {
        if (e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;

        replaceLore(e.getGui().getSlotUnderMouse().getStack());
    }
    
    private static void replaceLore(ItemStack stack) {
        // Soul Point Timer
        if (stack.getItem() == Items.NETHER_STAR && stack.getDisplayName().contains("Soul Point")) {
            List<String> lore = ItemUtils.getLore(stack);
            if (lore != null && !lore.isEmpty()) {
                if (lore.get(lore.size() - 1).contains("Time until next soul point: ")) {
                    lore.remove(lore.size() - 1);
                    lore.remove(lore.size() - 1);
                }
                lore.add("");
                int secondsUntilSoulPoint = PlayerInfo.getPlayerInfo().getTicksToNextSoulPoint() / 20;
                int minutesUntilSoulPoint = secondsUntilSoulPoint / 60;
                secondsUntilSoulPoint %= 60;
                lore.add(TextFormatting.AQUA + "Time until next soul point: " + TextFormatting.WHITE + minutesUntilSoulPoint + ":" + String.format("%02d", secondsUntilSoulPoint));
                NBTTagCompound nbt = stack.getTagCompound();
                NBTTagCompound display = nbt.getCompoundTag("display");
                NBTTagList tag = new NBTTagList();
                lore.forEach(s -> tag.appendTag(new NBTTagString(s)));
                display.setTag("Lore", tag);
                nbt.setTag("display", display);
                stack.setTagCompound(nbt);
                return;
            }
        }

        // Wynnic Translator
        if (!stack.getTagCompound().getBoolean("showWynnic") && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if (StringUtils.hasWynnic(ItemUtils.getStringLore(stack))) {
                NBTTagList loreList = ItemUtils.getLoreTag(stack);
                if (loreList != null) {
                    stack.getTagCompound().setTag("originalLore", loreList.copy());
                    boolean capital = true;
                    for (int index = 0; index < loreList.tagCount(); index++) {
                        String lore = loreList.getStringTagAt(index);
                        if (StringUtils.hasWynnic(lore)) {
                            String translated = "";
                            boolean colorCode = false;
                            for (char character : lore.toCharArray()) {
                                String translatedCharacter;
                                if (StringUtils.isWynnic(character)) {
                                    translatedCharacter = StringUtils.translateCharacterFromWynnic(character);
                                    if (capital && translatedCharacter.matches("[a-z]")) {
                                        translatedCharacter = String.valueOf(Character.toUpperCase(translatedCharacter.charAt(0)));
                                    }
                                } else {
                                    translatedCharacter = String.valueOf(character);
                                }

                                translated += translatedCharacter;

                                if (".?!".contains(translatedCharacter)) {
                                    capital = true;
                                } else if (translatedCharacter.equals("§")) {
                                    colorCode = true;
                                } else if (!translatedCharacter.equals(" ") && !colorCode) {
                                    capital = false;
                                } else if (colorCode) {
                                    colorCode = false;
                                }
                            }
                            loreList.set(index, new NBTTagString(translated));
                        }
                    }
                }
            }
            stack.getTagCompound().setBoolean("showWynnic", true);
        }

        if (stack.getTagCompound().getBoolean("showWynnic") && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey("originalLore")) {
                NBTTagCompound displayTag = tag.getCompoundTag("display");
                if (displayTag != null) {
                    displayTag.setTag("Lore", tag.getTag("originalLore"));
                }
                tag.removeTag("originalLore");
            }
            stack.getTagCompound().setBoolean("showWynnic", false);
        }
    }
}
