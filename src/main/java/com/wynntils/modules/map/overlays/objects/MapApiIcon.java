/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.google.gson.JsonObject;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Mappings;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.MapMarkerProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapApiIcon extends MapIcon {
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

    private MapMarkerProfile mmp;
    private int texPosX, texPosZ, texSizeX, texSizeZ;
    private float sizeX, sizeZ;
    private int zoomNeeded;

    private String translatedName;

    MapApiIcon(MapMarkerProfile mmp, MapConfig.IconTexture iconTexture) {
        JsonObject iconMapping = Mappings.Map.map_icons_mappings.get(iconTexture == MapConfig.IconTexture.Classic ? "CLASSIC" : "MEDIVAL").getAsJsonObject().get(mmp.getIcon()).getAsJsonObject();

        this.mmp = mmp;

        texPosX = iconMapping.get("texPosX").getAsInt();
        texPosZ = iconMapping.get("texPosZ").getAsInt();
        texSizeX = iconMapping.get("texSizeX").getAsInt();
        texSizeZ = iconMapping.get("texSizeZ").getAsInt();
        float size = iconMapping.get("size").getAsFloat();
        sizeX = (texSizeX - texPosX) / size;
        sizeZ = (texSizeZ - texPosZ) / size;
        zoomNeeded = iconMapping.get("zoomNeeded").getAsInt();

        translatedName = MAPMARKERNAME_TRANSLATION.get(mmp.getIcon());
    }

    @Override public AssetsTexture getTexture() {
        return Textures.Map.map_icons;
    }

    @Override public int getPosX() {
        return mmp.getX();
    }

    @Override public int getPosZ() {
        return mmp.getZ();
    }

    @Override public String getName() {
        return mmp.getName();
    }

    @Override public int getTexPosX() {
        return texPosX;
    }

    @Override public int getTexPosZ() {
        return texPosZ;
    }

    @Override public int getTexSizeX() {
        return texSizeX;
    }

    @Override public int getTexSizeZ() {
        return texSizeZ;
    }

    @Override public float getSizeX() {
        return sizeX;
    }

    @Override public float getSizeZ() {
        return sizeZ;
    }

    @Override public int getZoomNeeded() {
        return zoomNeeded;
    }

    @Override public boolean isEnabled() {
        return MapConfig.INSTANCE.enabledMapIcons.getOrDefault(translatedName, true);
    }

    public MapMarkerProfile getMapMarkerProfile() {
        return mmp;
    }

    // As an optimisation, so methods don't need to be called all the time
    @Override
    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier) {
        float sizeX = this.sizeX * sizeMultiplier;
        float sizeZ = this.sizeZ * sizeMultiplier;
        renderer.drawRectF(
                Textures.Map.map_icons,
                centreX - sizeX, centreZ - sizeZ,
                centreX + sizeX, centreZ + sizeZ,
                texPosX, texPosZ, texSizeX, texSizeZ
        );
    }

    private static List<MapIcon> classicApiMarkers = null;
    private static List<MapIcon> medievalApiMarkers = null;

    public static void resetApiMarkers() {
        if (Textures.Map.map_icons == null) return;
        if (classicApiMarkers == null) {
            classicApiMarkers = new ArrayList<>();
        } else {
            classicApiMarkers.clear();
        }
        if (medievalApiMarkers == null) {
            medievalApiMarkers = new ArrayList<>();
        } else {
            medievalApiMarkers.clear();
        }
        for (MapMarkerProfile mmp : WebManager.getMapMarkers()) {
            if (isApiMarkerValid(mmp, MapConfig.IconTexture.Classic)) classicApiMarkers.add(new MapApiIcon(mmp, MapConfig.IconTexture.Classic));
            if (isApiMarkerValid(mmp, MapConfig.IconTexture.Medieval)) medievalApiMarkers.add(new MapApiIcon(mmp, MapConfig.IconTexture.Medieval));
        }
    }

    public static void resetApiMarkers(MapConfig.IconTexture iconTexture) {
        if (Textures.Map.map_icons == null) return;
        List<MapIcon> markers;
        switch (iconTexture) {
            case Classic:
                if (classicApiMarkers == null) {
                    markers = classicApiMarkers = new ArrayList<>();
                } else {
                    markers = classicApiMarkers;
                    markers.clear();
                }
                break;
            case Medieval:
                if (medievalApiMarkers == null) {
                    markers = medievalApiMarkers = new ArrayList<>();
                } else {
                    markers = medievalApiMarkers;
                    markers.clear();
                }
                break;
            default:
                return;
        }
        for (MapMarkerProfile mmp : WebManager.getMapMarkers()) {
            if (isApiMarkerValid(mmp, iconTexture)) markers.add(new MapApiIcon(mmp, iconTexture));
        }
    }

    public static List<MapIcon> getApiMarkers(MapConfig.IconTexture iconTexture) {
        if (classicApiMarkers == null && medievalApiMarkers == null) resetApiMarkers();
        switch (iconTexture) {
            case Classic:
                if (classicApiMarkers == null) resetApiMarkers(MapConfig.IconTexture.Classic);
                return classicApiMarkers;
            case Medieval:
                if (medievalApiMarkers == null) resetApiMarkers(MapConfig.IconTexture.Medieval);
                return medievalApiMarkers;
            default:
                return null;
        }
    }

    private static boolean isApiMarkerValid(MapMarkerProfile mmp, MapConfig.IconTexture iconTexture) {
        return Mappings.Map.map_icons_mappings.get(iconTexture == MapConfig.IconTexture.Classic ? "CLASSIC" : "MEDIVAL").getAsJsonObject().has(mmp.getIcon());
    }
}
