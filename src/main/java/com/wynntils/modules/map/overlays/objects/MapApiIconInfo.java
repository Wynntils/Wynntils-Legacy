/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.google.gson.JsonObject;
import com.wynntils.core.framework.rendering.textures.Mappings;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.webapi.profiles.MapMarkerProfile;

import java.util.HashMap;

public class MapApiIconInfo extends MapIconInfo {
    private static final HashMap<String, String> MAPMARKERNAME_TRANSLATION = new HashMap<String, String>() {{
        put("Content_Dungeon", "Dungeons");
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
        put("Special_Rune", "Runes");
        put("Special_RootsOfCorruption", "Nether Portal");
        put("Content_UltimateDiscovery", "Ultimate Discovery");
        put("Content_Cave", "Caves");
        put("Content_GrindSpot", "Grind Spots");
        put("Merchant_Other", "Other Merchants");
        put("Special_LightRealm", "Light's Secret");
        put("Merchant_Emerald", "Emerald Merchant");
    }};

    private String translatedName;

    private MapApiIconInfo(MapMarkerProfile mmp, JsonObject iconMapping) {
        super(
                Textures.Map.map_icons, mmp.getName(),
                mmp.getX(), mmp.getZ(),
                iconMapping.get("size").getAsFloat(),
                iconMapping.get("texPosX").getAsInt(), iconMapping.get("texPosZ").getAsInt(),
                iconMapping.get("texSizeX").getAsInt(), iconMapping.get("texSizeZ").getAsInt(),
                iconMapping.get("zoomNeeded").getAsInt()
        );
    }

    MapApiIconInfo(MapMarkerProfile mmp, MapConfig.IconTexture iconTexture) {
        this(mmp, Mappings.Map.map_icons_mappings.get(iconTexture == MapConfig.IconTexture.Classic ? "CLASSIC" : "MEDIVAL").getAsJsonObject().get(mmp.getIcon()).getAsJsonObject());
    }

    public boolean isEnabled() {
        return MapConfig.INSTANCE.enabledMapIcons.getOrDefault(translatedName, true);
    }
}
