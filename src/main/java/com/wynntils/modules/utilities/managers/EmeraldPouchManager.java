package com.wynntils.modules.utilities.managers;

import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import net.minecraft.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmeraldPouchManager {

    private static final String EB = EmeraldSymbols.E_STRING + EmeraldSymbols.B_STRING;
    private static final String LE = EmeraldSymbols.L_STRING + EmeraldSymbols.E_STRING;
    private static final Pattern POUCH_CAPACITY_PATTERN = Pattern.compile("\\(([0-9]+)(" + EB + "|" + LE + "|stx) Total\\)");
    private static final Pattern POUCH_USAGE_PATTERN = Pattern.compile("§6§l([0-9]* ?[0-9]* ?[0-9]*)" + EmeraldSymbols.E_STRING);
    private static final Pattern POUCH_TIER_PATTERN = Pattern.compile("§aEmerald Pouch§2 \\[Tier ([XIV]{1,4})]");


    public static boolean isEmeraldPouch(ItemStack i) {
        return i.getDisplayName().startsWith("§aEmerald Pouch§2 [Tier");
    }

    public static int getPouchCapacity(ItemStack i) {
        Matcher capacityMatcher = POUCH_CAPACITY_PATTERN.matcher(ItemUtils.getStringLore(i));
        if (!capacityMatcher.find()) {
            return -1;
        }
        int capacity = Integer.parseInt(capacityMatcher.group(1)) * 64;
        if (capacityMatcher.group(2).equals(LE)) capacity *= 64;
        if (capacityMatcher.group(2).equals("stx")) capacity *= 4096;
        return capacity;
    }

    public static int getPouchUsage(ItemStack i) {
        Matcher usageMatcher = POUCH_USAGE_PATTERN.matcher(ItemUtils.getStringLore(i));
        if (!usageMatcher.find()) {
            return -1;
        }
        return Integer.parseInt(usageMatcher.group(1).replaceAll("\\s", ""));
    }

    public static String getPouchTier(ItemStack i) {
        Matcher tierMatcher = POUCH_TIER_PATTERN.matcher(i.getDisplayName());
        if (!tierMatcher.find()) {
            return null;
        }
        return tierMatcher.group(1);
    }

}