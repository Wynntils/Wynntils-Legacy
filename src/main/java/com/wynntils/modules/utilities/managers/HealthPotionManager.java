package com.wynntils.modules.utilities.managers;

import com.wynntils.core.utils.ItemUtils;
import net.minecraft.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HealthPotionManager {

    private static final Pattern HEAL_AMOUNT_PATTERN = Pattern.compile("§c- \\+(\\d+) . §4\\[\\d+/\\d+]§7 \\(Tier [XIV]{1,5}\\)");

    public static boolean isHealthPotion(ItemStack i) {
        if (i.isEmpty() || !i.hasDisplayName()) return false;
        return i.getDisplayName().contains("§dPotions of Healing") || i.getDisplayName().contains("§cPotion of Healing");
    }

    public static int getNextHealAmount(ItemStack i) {
        Matcher healMatcher = HEAL_AMOUNT_PATTERN.matcher(ItemUtils.getStringLore(i));
        if (!healMatcher.find()) {
            return -1;
        }
        return Integer.parseInt(healMatcher.group(1));
    }
}
