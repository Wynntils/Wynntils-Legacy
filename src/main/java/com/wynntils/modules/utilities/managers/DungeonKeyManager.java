package com.wynntils.modules.utilities.managers;

import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.objects.Pair;
import net.minecraft.item.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DungeonKeyManager {

    private static final Pattern NORMAL_KEY_PATTERN = Pattern.compile("§7Grants access to the§f(.+)§7 once");
    private static final Pattern BROKEN_KEY_PATTERN = Pattern.compile("§7Use this item at the§fForgery§7 to craft a§4(.+) Key");
    public static final int forgeryX = -873;
    public static final int forgeryZ = -4902;
    private static final Pattern KEY_COORDINATE_PATTERN = Pattern.compile("§7Coordinates: §f(-?\\d+), (-?\\d+)");

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
                        lore.contains("§7Use this item at§7the §fForgery §7to craft§7a §4Corrupted Dungeon Key") ||
                        lore.contains("§aDungeon Info"));
    }

    public static boolean isCorrupted(ItemStack is) {
        return isDungeonKey(is) && (is.getDisplayName().contains("Corrupted") || isBroken(is)); // Broken keys are always corrupted
    }

    public static boolean isBroken(ItemStack is) {
        return isDungeonKey(is) && is.getDisplayName().contains("Broken");
    }

    public static Pair<Integer, Integer> getDungeonCoords(ItemStack is) {
        if (isCorrupted(is)) {
            return new Pair<>(forgeryX, forgeryZ);
        }

        // So the EO key currently points to FF for some reason, see
        // https://cdn.discordapp.com/attachments/743928502314598511/963592548138492014/unknown.png
        // Manually override for now
        if (getDungeonKey(is) == DungeonKey.EldritchOutlook) {
            return new Pair<>(1291, -748);
        }

        Matcher m = KEY_COORDINATE_PATTERN.matcher(ItemUtils.getStringLore(is));
        if (!m.find()) return null;
        return new Pair<>(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
    }

    public static DungeonKey getDungeonKey(ItemStack is) {
        if (isBroken(is)) { // Broken corrupted keys
            String lore = ItemUtils.getStringLore(is);
            String result;
            Matcher brokenMatcher = BROKEN_KEY_PATTERN.matcher(lore);
            if (!brokenMatcher.find()) {
                // For broken keys, eg. from raid rewards
                result = is.getDisplayName().replace("Broken ", "").replace(" Key", "");
                if (!result.contains("Corrupted")) {
                    result = "Corrupted " + result;
                }
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
