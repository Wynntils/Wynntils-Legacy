package com.wynntils.modules.utilities.managers;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorkianAmplifierManager {

    public static final Pattern CORKIAN_AMPLIFIER_PATTERN = Pattern.compile("§bCorkian Amplifier (I{1,3})");

    public static boolean isAmplifier(ItemStack i) {
        return i.getDisplayName().startsWith("§bCorkian Amplifier ") && i.getItem() == Item.getItemFromBlock(Blocks.STONE_BUTTON);
    }

    public static String getAmplifierTier(ItemStack i) {
        Matcher tierMatcher = CORKIAN_AMPLIFIER_PATTERN.matcher(i.getDisplayName());
        if (!tierMatcher.find()) {
            return null;
        }
        return tierMatcher.group(1);
    }
}
