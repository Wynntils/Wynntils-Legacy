/*
 *  * Copyright © Wynntils - 2018 - 2021.
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
import com.wynntils.modules.utilities.managers.EmeraldPouchManager;
import com.wynntils.modules.utilities.managers.KeyManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemLevelOverlay implements Listener {

    public static final Pattern EMERALD_POUCH_PATTERN = Pattern.compile("§aEmerald Pouch§2 \\[Tier ([XIV]{1,4})]");
    public static final Pattern CORKIAN_AMPLIFIER_PATTERN = Pattern.compile("§bCorkian Amplifier (I{1,3})");

    private String romanToArabic(String romanNumeral) {
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
        if (!UtilitiesConfig.Items.INSTANCE.itemLevelOverlayOutsideGui && McIf.mc().currentScreen == null) return;
        if (!KeyManager.getShowLevelOverlayKey().isKeyDown()) return;

        ItemStack stack = event.getStack();
        Item item = stack.getItem();
        String name = stack.getDisplayName();

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
        if (item == Item.getItemFromBlock(Blocks.STONE_BUTTON)) {
            Matcher amplifierMatcher = CORKIAN_AMPLIFIER_PATTERN.matcher(name);
            if (amplifierMatcher.find()) {
                if (!UtilitiesConfig.Items.INSTANCE.levelKeyShowsItemTiers) return;
                if (UtilitiesConfig.Items.INSTANCE.romanNumeralItemTier) {
                    event.setOverlayText(amplifierMatcher.group(1));
                    return;
                }
                event.setOverlayText(romanToArabic(amplifierMatcher.group(1)));
                return;
            }
        }

        String lore = ItemUtils.getStringLore(stack);

        // item level
        IntRange level = ItemUtils.getLevel(lore);
        if (level != null) {
            event.setOverlayText(UtilitiesConfig.Items.INSTANCE.averageUnidentifiedLevel ? level.toString() : Integer.toString(level.getAverage()));
            return;
        }
    }

}
