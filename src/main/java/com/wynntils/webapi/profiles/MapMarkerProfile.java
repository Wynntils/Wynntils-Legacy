/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.profiles;

import com.wynntils.Reference;
import com.wynntils.core.utils.StringUtils;

import java.util.*;

public class MapMarkerProfile extends LocationProfile {

    private static final Map<String, String> MAPMARKERNAME_TRANSLATION = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("Content_Dungeon", "Dungeons");
        put("Content_CorruptedDungeon", "Corrupted Dungeons");
        put("Content_BossAltar", "Boss Altar");
        put("Merchant_Accessory", "Accessory Merchant");
        put("Merchant_Armour", "Armour Merchant");
        put("Merchant_Dungeon", "Dungeon Merchant");
        put("Merchant_Horse", "Horse Merchant");
        put("Merchant_KeyForge", "Key Forge Merchant");
        put("Merchant_Liquid", "LE Merchant");
        put("Merchant_Potion", "Potion Merchant");
        put("Merchant_Powder", "Powder Merchant");
        put("Merchant_Scroll", "Scroll Merchant");
        put("Merchant_Seasail", "Seasail Merchant");
        put("Merchant_Weapon", "Weapon Merchant");
        put("NPC_Blacksmith", "Blacksmith");
        put("NPC_GuildMaster", "Guild Master");
        put("NPC_ItemIdentifier", "Item Identifier");
        put("NPC_PowderMaster", "Powder Master");
        put("Special_FastTravel", "Fast Travel");
        put("tnt", "TNT Merchant");
        put("painting", "Art Merchant");
        put("Ore_Refinery", "Ore Refinery");
        put("Fish_Refinery", "Fish Refinery");
        put("Wood_Refinery", "Wood Refinery");
        put("Crop_Refinery", "Crop Refinery");
        put("NPC_TradeMarket", "Marketplace");
        put("Content_Quest", "Quests");
        put("Content_Miniquest", "Mini-Quests");
        put("Special_Rune", "Runes");
        put("Special_RootsOfCorruption", "Nether Portal");
        put("Content_UltimateDiscovery", "Ultimate Discovery");
        put("Content_Cave", "Caves");
        put("Content_GrindSpot", "Grind Spots");
        put("Merchant_Other", "Other Merchants");
        put("Special_LightRealm", "Light's Secret");
        put("Merchant_Emerald", "Emerald Merchant");
        put("Profession_Weaponsmithing", "Weaponsmithing Station");
        put("Profession_Armouring", "Armouring Station");
        put("Profession_Alchemism", "Alchemism Station");
        put("Profession_Jeweling", "Jeweling Station");
        put("Profession_Tailoring", "Tailoring Station");
        put("Profession_Scribing", "Scribing Station");
        put("Profession_Cooking", "Cooking Station");
        put("Profession_Woodworking", "Woodworking Station");
        put("Merchant_Tool", "Tool Merchant");
    }});

    private static final Map<String, String> MAPMARKERNAME_REVERSE_TRANSLATION = Collections.unmodifiableMap(new HashMap<String, String>(MAPMARKERNAME_TRANSLATION.size()) {{
        for (Entry<String, String> entry : MAPMARKERNAME_TRANSLATION.entrySet()) {
            this.put(entry.getValue(), entry.getKey());
        }
    }});

    private static final Set<String> IGNORED_MARKERS = Collections.unmodifiableSet(new HashSet<String>() {{
        for (String ignored : new String[]{
            "Content_CorruptedDungeon"
        }) {
            add(ignored);
            String translated = MAPMARKERNAME_TRANSLATION.get(ignored);
            assert translated != null;
            add(translated);
        }
    }});

    int y;
    String icon;

    public MapMarkerProfile(String name, int x, int y, int z, String icon) {
        super(name, x, z);
        this.y = y;
        this.icon = icon;
        ensureNormalized();
    }

    public boolean isIgnored() {
        return IGNORED_MARKERS.contains(this.name);
    }

    public int getY() {
        return y;
    }

    public String getIcon() {
        return icon;
    }

    public void ensureNormalized() {
        if (name != null) name = StringUtils.normalizeBadString(name);
        icon = icon.replace(".png", "");
    }

    @Override
    public String getTranslatedName() {
        return MAPMARKERNAME_TRANSLATION.get(icon);
    }

    public static String getReverseTranslation(String icon) {
        String reverse = MAPMARKERNAME_REVERSE_TRANSLATION.getOrDefault(icon, icon);
        if (!MAPMARKERNAME_TRANSLATION.containsKey(reverse)) {
            throw new RuntimeException("getReverseTranslation(\"" + icon + "\"): invalid name");
        }
        return reverse;
    }

    /*
     * Debug function run in developmentEnvironment to verify consistency
     */
    public static void validateIcons(Map<String, Boolean> enabledIcons) {
        for (String icon : MAPMARKERNAME_TRANSLATION.values()) {
            if (IGNORED_MARKERS.contains(icon)) continue;
            if (!enabledIcons.containsKey(icon)) Reference.LOGGER.warn("Missing option for icon \"" + icon + "\"");
        }
        for (String icon : enabledIcons.keySet()) {
            if (IGNORED_MARKERS.contains(icon)) continue;
            if (!MAPMARKERNAME_REVERSE_TRANSLATION.containsKey(icon)) Reference.LOGGER.warn("Missing translation for \"" + icon + "\"");
        }
    }
}
