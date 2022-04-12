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
        EldritchOutlook("EO", "Eldritch Outlook"),

        CDecrepitSewers("DS", "Corrupted Decrepit Sewers"),
        CInfestedPit("IP", "Corrupted Infested Pit"),
        CLostSanctuary("LS", "Corrupted Lost Sanctuary"),
        CUnderworldCrypt("UC", "Corrupted Underworld Crypt"),
        CSandSweptTob("ST", "Corrupted Sand-Swept Tomb"),
        CIceBarrows("IB", "Corrupted Ice Barrows"),
        CUndergrowthRuins("UR", "Corrupted Undergrowth Ruins");


        public final String acronym;
        public final String fullname;

        DungeonKey(String acronym, String fullname) {
            this.acronym = acronym;
            this.fullname = fullname;
        }
    }

    public static boolean isDungeonKey(ItemStack is) {
        String lore = ItemUtils.getStringLore(is);
        return lore.contains("Dungeon Info");
    }

    public static boolean isCorrupted(ItemStack is) {
        return is.getDisplayName().contains("Corrupted") && is.getDisplayName().contains("Key");
    }

    public static boolean isBroken(ItemStack is) {
        return is.getDisplayName().contains("Broken") && is.getDisplayName().contains("Key");
    }

    public static DungeonKey getKeyDungeon(ItemStack is) {
        if (isBroken(is)) { // Broken corrupted keys
            Matcher brokenMatcher = BROKEN_KEY_PATTERN.matcher(ItemUtils.getStringLore(is));
            if (!brokenMatcher.find()) return null;
            for (DungeonKey dk : DungeonKey.values()) {
                if (brokenMatcher.group(1).equals(dk.fullname)) {
                    return dk;
                }
            }
        } else { // Fixed corrupted keys, normal keys
            Matcher normalMatcher = NORMAL_KEY_PATTERN.matcher(ItemUtils.getStringLore(is));
            if (!normalMatcher.find()) return null;
            for (DungeonKey dk : DungeonKey.values()) {
                if (normalMatcher.group(1).equals(dk.fullname)) {
                    return dk;
                }
            }
        }
        return null;
    }
}
