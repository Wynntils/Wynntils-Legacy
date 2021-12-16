package com.wynntils.modules.utilities.managers;

import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import net.minecraft.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmeraldPouchManager {

    private static final Pattern POUCH_CAPACITY_PATTERN = Pattern.compile("\\(([0-9]+)(" + EmeraldSymbols.BLOCKS + "|" + EmeraldSymbols.LE + "|stx) Total\\)");
    private static final Pattern POUCH_USAGE_PATTERN = Pattern.compile("§6§l([0-9]* ?[0-9]* ?[0-9]*)" + EmeraldSymbols.E_STRING);


    public static boolean isEmeraldPouch(ItemStack i) {
        return i.getDisplayName().startsWith("§aEmerald Pouch§2 [Tier");
    }

    public static int getPouchCapacity(ItemStack i) {
        Matcher capacityMatcher = POUCH_CAPACITY_PATTERN.matcher(ItemUtils.getStringLore(i));
        if (!capacityMatcher.find()) {
            return -1;
        }
        int capacity = Integer.parseInt(capacityMatcher.group(1)) * 64;
        if (capacityMatcher.group(2).equals(EmeraldSymbols.LE)) capacity *= 64;
        if (capacityMatcher.group(2).equals("stx")) capacity *= 4096;
        return capacity;
    }

    public static int getPouchUsage(ItemStack i) {
        Matcher usageMatcher = POUCH_USAGE_PATTERN.matcher(ItemUtils.getStringLore(i));
        if (!usageMatcher.find()) {
            if (ItemUtils.getStringLore(i).contains("§7Empty")) { // We might just have an valid, empty pouch
                return 0;
            }

            return -1;
        }
        return Integer.parseInt(usageMatcher.group(1).replaceAll("\\s", ""));
    }

}
