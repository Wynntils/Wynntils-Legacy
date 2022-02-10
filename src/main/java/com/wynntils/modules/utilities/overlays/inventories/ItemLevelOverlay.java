/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.enums.Powder;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.IntRange;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.CorkianAmplifierManager;
import com.wynntils.modules.utilities.managers.EmeraldPouchManager;
import com.wynntils.modules.utilities.managers.KeyManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;

public class ItemLevelOverlay implements Listener {

    public static String romanToArabic(String romanNumeral) {
        String num = "0";
        switch (romanNumeral) {
            case "I":
                num = "1";
                break;
            case "II":
                num = "2";
                break;
            case "III":
                num = "3";
                break;
            case "IV":
                num = "4";
                break;
            case "V":
                num = "5";
                break;
            case "VI":
                num = "6";
                break;
            case "VII":
                num = "7";
                break;
            case "VIII":
                num = "8";
                break;
            case "IX":
                num = "9";
                break;
            case "X":
                num = "10";
                break;
        }
        return num;
    }

    @SubscribeEvent
    public void onItemOverlay(RenderEvent.DrawItemOverlay event) {
        if (!UtilitiesConfig.INSTANCE.showConsumableChargesHotbar && !UtilitiesConfig.Items.INSTANCE.itemLevelOverlayOutsideGui && McIf.mc().currentScreen == null) return;
        ItemStack stack = event.getStack();
        Item item = stack.getItem();
        String name = stack.getDisplayName();

        if (KeyManager.getShowLevelOverlayKey().isKeyDown()) {
            // powder tier
            if (item == Items.DYE || item == Items.GUNPOWDER || item == Items.CLAY_BALL || item == Items.SUGAR) {
                Matcher powderMatcher = Powder.POWDER_NAME_PATTERN.matcher(StringUtils.normalizeBadString(name));
                if (powderMatcher.find()) {
                    if (!UtilitiesConfig.Items.INSTANCE.levelKeyShowsItemTiers) return;
                    if (UtilitiesConfig.Items.INSTANCE.romanNumeralItemTier) {
                        event.setOverlayText(powderMatcher.group(1));
                        return;
                    }
                    event.setOverlayText(romanToArabic(powderMatcher.group(1)));
                    return;
                }
            }

            // emerald pouch tier
            if (EmeraldPouchManager.isEmeraldPouch(stack)) {
                if (!UtilitiesConfig.Items.INSTANCE.levelKeyShowsItemTiers) return;
                if (UtilitiesConfig.Items.INSTANCE.romanNumeralItemTier) {
                    event.setOverlayText(EmeraldPouchManager.getPouchTier(stack));
                    return;
                }
                event.setOverlayText(romanToArabic(EmeraldPouchManager.getPouchTier(stack)));
                return;
            }

            // cork amp tier
            if (CorkianAmplifierManager.isAmplifier(stack)) {
                if (UtilitiesConfig.Items.INSTANCE.romanNumeralItemTier) {
                    event.setOverlayText(CorkianAmplifierManager.getAmplifierTier(stack));
                    return;
                }
                event.setOverlayText(romanToArabic(CorkianAmplifierManager.getAmplifierTier(stack)));
                return;
            }

            String lore = ItemUtils.getStringLore(stack);

            // item level
            IntRange level = ItemUtils.getLevel(lore);
            if (level != null) {
                event.setOverlayText(UtilitiesConfig.Items.INSTANCE.averageUnidentifiedLevel ? level.toString() : Integer.toString(level.getAverage()));
                return;
            }
        }

        // Hotbar charges
        if (UtilitiesConfig.INSTANCE.showConsumableChargesHotbar && (item == Items.POTIONITEM || item == Items.DIAMOND_AXE)) { // potion charge count in hotbar
            if (!name.contains("[") || !name.contains("/")) return; // Make sure it's actually some consumable
            String[] consumable = name.split(" ");
            String[] charges = consumable[consumable.length - 1].split("/");
            String remainingCharges = charges[0].replace("[", "");
            event.setOverlayText(TextFormatting.getTextWithoutFormattingCodes(remainingCharges));
        }
    }

}
