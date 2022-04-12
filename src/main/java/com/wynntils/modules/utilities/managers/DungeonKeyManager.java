package com.wynntils.modules.utilities.managers;

import com.wynntils.core.utils.ItemUtils;
import net.minecraft.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DungeonKeyManager {

    private static final Pattern NORMAL_KEY_PATTERN = Pattern.compile("§7Grants access to the§f(.+)§7 once");
    private static final Pattern BROKEN_KEY_PATTERN = Pattern.compile("§7Use this item at the§fForgery§7 to craft a§4(.+) Key");

    public enum DungeonKey {
        DecrepitSewers("DS", "Decrepit Sewers"),
        InfestedPit("IP", "Infested Pit"),
        LostSanctuary("LS", "Lost Sanctuary"),
        UnderworldCrypt("UC", "Underworld Crypt"),
        SandSweptTomb("ST", "Sand-Swept Tomb"),
        IceBarrows("IB", "Ice Barrows"),
        UndergrowthRuins("UR", "Undergrowth Ruins"),
        GalleonsGraveyard("GG", "Galleon's Graveyard"),
        FallenFactory("FF", "Fallen Factory"),
        EldritchOutlook("EO", "Eldritch Outlook");

        public final String acronym;
        private final String fullName;

        DungeonKey(String acronym, String fullName) {
            this.acronym = acronym;
            this.fullName = fullName;
        }

        public String getFullName(boolean isCorrupted) {
            return (isCorrupted) ? "Corrupted " + this.fullName : this.fullName;
        }
    }

    public static boolean isDungeonKey(ItemStack is) {
        String lore = ItemUtils.getStringLore(is);
        return is.getDisplayName().contains("Key") &&
                (lore.contains("§7Grants access to the") ||
                        lore.contains("§7Use this item at the §fForgery§7 to craft a") ||
                        lore.contains("§7Use this item at§7the §fForgery §7to craft§7a §4Corrupted Dungeon Key"));
    }

    public static boolean isCorrupted(ItemStack is) {
        return isDungeonKey(is) && (is.getDisplayName().contains("Corrupted") || isBroken(is)); // Broken keys are always corrupted
    }

    public static boolean isBroken(ItemStack is) {
        return isDungeonKey(is) && is.getDisplayName().contains("Broken");
    }

    public static DungeonKey getKeyDungeon(ItemStack is) {
        if (isBroken(is)) { // Broken corrupted keys
            String lore = ItemUtils.getStringLore(is);
            String result;
            Matcher brokenMatcher = BROKEN_KEY_PATTERN.matcher(lore);
            if (!brokenMatcher.find()) {
                if (!lore.contains("§7Use this item at§7the §fForgery §7to craft§7a §4Corrupted Dungeon Key")) return null;
                // This lore is only present for keys that aren't working properly and were dropped from raids
                // Only way to parse this is to use item name
                result = is.getDisplayName().replace("Broken", "Corrupted").replace(" Key", "");
            } else {
                result = brokenMatcher.group(1);
            }
            for (DungeonKey dk : DungeonKey.values()) {
                if (result.equals(dk.getFullName(true))) {
                    return dk;
                }
            }
        } else { // Fixed corrupted keys, normal keys
            Matcher normalMatcher = NORMAL_KEY_PATTERN.matcher(ItemUtils.getStringLore(is));
            if (!normalMatcher.find()) return null;
            for (DungeonKey dk : DungeonKey.values()) {
                if (normalMatcher.group(1).equals(dk.getFullName(isCorrupted(is)))) {
                    return dk;
                }
            }
        }
        return null;
    }
}
