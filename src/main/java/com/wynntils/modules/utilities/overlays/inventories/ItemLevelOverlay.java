/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemLevelOverlay implements Listener {

    private static final Pattern POWDER_NAME_PATTERN = Pattern.compile("(?:Earth|Thunder|Water|Fire|Air|Blank) Powder (IV|V|VI|I{1,3})");
    private static final Pattern CRAFTING_LEVEL_PATTERN = Pattern.compile("Crafting Lv\\. Min: ([0-9]+)");

    @SubscribeEvent
    public void onItemOverlay(RenderEvent.DrawItemOverlay event) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) return;

        ItemStack stack = event.getStack();
        Item item = stack.getItem();
        String name = stack.getDisplayName();

        // powder tier
        if (item == Items.DYE || item == Items.GUNPOWDER || item == Items.CLAY_BALL || item == Items.SUGAR) {
            Matcher powderMatcher = POWDER_NAME_PATTERN.matcher(StringUtils.normalizeBadString(name));
            if (powderMatcher.find()) {
                event.setOverlayText(powderMatcher.group(1));
                return;
            }
        }

        String lore = ItemUtils.getStringLore(stack);

        // identifiable item combat level
        ItemUtils.CombatLevel level = ItemUtils.getLevel(lore);
        if (level != null) {
            event.setOverlayText(UtilitiesConfig.Items.INSTANCE.averageUnidentifiedLevel ? level.toString() : Integer.toString(level.getAverage()));
            return;
        }

        // ingredient crafting level
        Matcher craftLevelMatcher = CRAFTING_LEVEL_PATTERN.matcher(lore);
        if (craftLevelMatcher.find()) {
            event.setOverlayText(craftLevelMatcher.group(1));
            return;
        }
    }

}
