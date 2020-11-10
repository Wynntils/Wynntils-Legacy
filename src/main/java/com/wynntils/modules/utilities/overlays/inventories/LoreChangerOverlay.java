/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
    public void onChest(GuiOverlapEvent.ChestOverlap.DrawScreen.Post e) {
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
        if ((stack.getItem() == Items.NETHER_STAR || stack.getItem() == Item.getItemFromBlock(Blocks.SNOW_LAYER)) && stack.getDisplayName().contains("Soul Point")) {
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
                ItemUtils.replaceLore(stack, lore);
                return;
            }
        }

        // Wynnic Translator
        if (stack.hasTagCompound() && !stack.getTagCompound().getBoolean("showWynnic") && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if (StringUtils.hasWynnic(ItemUtils.getStringLore(stack))) {
                NBTTagList loreList = ItemUtils.getLoreTag(stack);
                if (loreList != null) {
                    stack.getTagCompound().setTag("originalLore", loreList.copy());
                    boolean capital = true;
                    for (int index = 0; index < loreList.tagCount(); index++) {
                        String lore = loreList.getStringTagAt(index);
                        if (StringUtils.hasWynnic(lore)) {
                            StringBuilder translated = new StringBuilder();
                            boolean colorCode = false;
                            StringBuilder number = new StringBuilder();
                            for (char character : lore.toCharArray()) {
                                if (StringUtils.isWynnicNumber(character)) {
                                    number.append(character);
                                } else {
                                    if (!number.toString().isEmpty()) {
                                        translated.append(StringUtils.translateNumberFromWynnic(number.toString()));
                                        number = new StringBuilder();
                                    }

                                    String translatedCharacter;
                                    if (StringUtils.isWynnic(character)) {
                                        translatedCharacter = StringUtils.translateCharacterFromWynnic(character);
                                        if (capital && translatedCharacter.matches("[a-z]")) {
                                            translatedCharacter = String.valueOf(Character.toUpperCase(translatedCharacter.charAt(0)));
                                        }
                                    } else {
                                        translatedCharacter = String.valueOf(character);
                                    }

                                    translated.append(translatedCharacter);

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
                            }
                            if (!number.toString().isEmpty()) {
                                translated.append(StringUtils.translateNumberFromWynnic(number.toString()));
                                number = new StringBuilder();
                            }

                            loreList.set(index, new NBTTagString(translated.toString()));
                        }
                    }
                }
            }
            stack.getTagCompound().setBoolean("showWynnic", true);
        }

        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("showWynnic") && !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
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
