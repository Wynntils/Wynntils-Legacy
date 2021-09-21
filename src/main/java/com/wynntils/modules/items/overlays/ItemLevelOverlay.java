/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.items.overlays;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.objects.IntRange;
import com.wynntils.modules.items.configs.ItemsConfig;
import com.wynntils.modules.utilities.managers.KeyManager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemLevelOverlay implements Listener {

    private static final Pattern POWDER_NAME_PATTERN = Pattern.compile("(?:Earth|Thunder|Water|Fire|Air|Blank) Powder (VI|IV|V|I{1,3})");

    @SubscribeEvent
    public void onItemOverlay(RenderEvent.RenderItem event) {
        if (!ItemsConfig.Items.INSTANCE.itemLevelOverlayOutsideGui && McIf.mc().currentScreen == null) return;
        if (!KeyManager.getShowLevelOverlayKey().isKeyDown()) return;

        ItemStack stack = event.getStack();
        Item item = stack.getItem();
        String name = stack.getDisplayName();

        // powder tier
        if (item == Items.DYE || item == Items.GUNPOWDER || item == Items.CLAY_BALL || item == Items.SUGAR) {
            Matcher powderMatcher = POWDER_NAME_PATTERN.matcher(name);
            if (powderMatcher.find()) {
                if (ItemsConfig.Items.INSTANCE.romanNumeralPowderTier) {
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

        // item level
        IntRange level = ItemUtils.getLevel(lore);
        if (level != null) {
            event.setOverlayText(ItemsConfig.Items.INSTANCE.averageUnidentifiedLevel ? level.toString() : Integer.toString(level.getAverage()));
        }
    }

}
