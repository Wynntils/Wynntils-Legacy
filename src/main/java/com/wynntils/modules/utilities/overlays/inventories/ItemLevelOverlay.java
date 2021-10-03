/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.IntRange;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.managers.KeyManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemLevelOverlay implements Listener {

    private static final Pattern POWDER_NAME_PATTERN = Pattern.compile("(?:Earth|Thunder|Water|Fire|Air|Blank) Powder (VI|IV|V|I{1,3})");
    private static final Pattern EMERALD_POUCH_PATTERN = Pattern.compile("Emerald Pouch§2 \\[Tier (IX|X|VI{1,3}|IV|V|I{1,3})]");

    @SubscribeEvent
    public void onItemOverlay(RenderEvent.DrawItemOverlay event) {
        if (!UtilitiesConfig.Items.INSTANCE.itemLevelOverlayOutsideGui && McIf.mc().currentScreen == null) return;
        if (!KeyManager.getShowLevelOverlayKey().isKeyDown()) return;

        ItemStack stack = event.getStack();
        Item item = stack.getItem();
        String name = stack.getDisplayName();

        // powder tier
        if (item == Items.DYE || item == Items.GUNPOWDER || item == Items.CLAY_BALL || item == Items.SUGAR) {
            Matcher powderMatcher = POWDER_NAME_PATTERN.matcher(StringUtils.normalizeBadString(name));
            if (powderMatcher.find()) {
                if (UtilitiesConfig.Items.INSTANCE.romanNumeralPowderTier) {
                    event.setOverlayText(powderMatcher.group(1));
                    return;
                }
                int tier = 0;
                switch (powderMatcher.group(1)) {
                    case "I":
                        tier = 1;
                        break;
                    case "II":
                        tier = 2;
                        break;
                    case "III":
                        tier = 3;
                        break;
                    case "IV":
                        tier = 4;
                        break;
                    case "V":
                        tier = 5;
                        break;
                    case "VI":
                        tier = 6;
                        break;
                }
                event.setOverlayText(Integer.toString(tier));
                return;
            }
        }

        String lore = ItemUtils.getStringLore(stack);

        // emerald pouch tier
        if (item == Items.DIAMOND_AXE) {
            Matcher emeraldPouchMatcher = EMERALD_POUCH_PATTERN.matcher(StringUtils.normalizeBadString(name));
            if (emeraldPouchMatcher.find()) {
                if (UtilitiesConfig.Items.INSTANCE.romanNumeralEmeraldPouchTier) {
                    event.setOverlayText(emeraldPouchMatcher.group(1));
                    return;
                }
                int tier = 0;
                switch (emeraldPouchMatcher.group(1)) {
                    case "I":
                        tier = 1;
                        break;
                    case "II":
                        tier = 2;
                        break;
                    case "III":
                        tier = 3;
                        break;
                    case "IV":
                        tier = 4;
                        break;
                    case "V":
                        tier = 5;
                        break;
                    case "VI":
                        tier = 6;
                        break;
                    case "VII":
                        tier = 7;
                        break;
                    case "VIII":
                        tier = 8;
                        break;
                    case "IX":
                        tier = 9;
                        break;
                    case "X":
                        tier = 10;
                        break;
                }
                event.setOverlayText(Integer.toString(tier));
                return;
            }
        }

        // item level
        IntRange level = ItemUtils.getLevel(lore);
        if (level != null) {
            event.setOverlayText(UtilitiesConfig.Items.INSTANCE.averageUnidentifiedLevel ? level.toString() : Integer.toString(level.getAverage()));
            return;
        }
    }

}
