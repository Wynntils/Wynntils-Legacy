/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.overlays.objects;

import com.google.gson.JsonObject;
import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Mappings;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.MapMarkerProfile;

import java.util.*;

public class MapApiIcon extends MapTextureIcon {

    public static final Map<String, String> MAPMARKERNAME_TRANSLATION = Collections.unmodifiableMap(new HashMap<String, String>() {{
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

    public static final Set<String> IGNORED_MARKERS = Collections.unmodifiableSet(new HashSet<String>() {{
        for (String ignored : new String[]{
            "Content_CorruptedDungeon"
        }) {
            add(ignored);
            String translated = MAPMARKERNAME_TRANSLATION.get(ignored);
            assert translated != null;
            add(translated);
        }
    }});

    public static final Map<String, String> MAPMARKERNAME_REVERSE_TRANSLATION = Collections.unmodifiableMap(new HashMap<String, String>(MAPMARKERNAME_TRANSLATION.size()) {{
        for (HashMap.Entry<String, String> entry : MAPMARKERNAME_TRANSLATION.entrySet()) {
            this.put(entry.getValue(), entry.getKey());
        }
    }});

    private MapMarkerProfile mmp;
    private int texPosX, texPosZ, texSizeX, texSizeZ;
    private float sizeX, sizeZ;
    private int zoomNeeded;

    private String translatedName;

    MapApiIcon(MapMarkerProfile mmp, MapConfig.IconTexture iconTexture) {
        JsonObject iconMapping = Mappings.Map.map_icons_mappings.get(iconTexture == MapConfig.IconTexture.Classic ? "CLASSIC" : "MEDIEVAL").getAsJsonObject().get(mmp.getIcon()).getAsJsonObject();

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

    @Override public boolean isEnabled(boolean forMinimap) {
        if (MapConfig.INSTANCE.hideCompletedQuests && (mmp.getIcon().equals("Content_Quest") || mmp.getIcon().equals("Content_Miniquest"))) {
            QuestInfo questData = QuestManager.getCurrentQuestsData().get(mmp.getName());
            if (questData != null && questData.getStatus() == QuestStatus.COMPLETED) {
                return false;
            }
        }

        Boolean enabled = (forMinimap ? MapConfig.INSTANCE.enabledMinimapIcons : MapConfig.INSTANCE.enabledMapIcons).get(translatedName);

        if (enabled == null) {
            // Missing some keys; Add them all
            HashMap<String, Boolean> defaulted = MapConfig.resetMapIcons(false);
            for (Map.Entry<String, Boolean> e : defaulted.entrySet()) {
                String icon = e.getKey();
                if (MapConfig.INSTANCE.enabledMapIcons.get(icon) == null) {
                    MapConfig.INSTANCE.enabledMapIcons.put(icon, e.getValue());
                }
            }

            defaulted = MapConfig.resetMapIcons(true);
            for (Map.Entry<String, Boolean> e : defaulted.entrySet()) {
                String icon = e.getKey();
                if (MapConfig.INSTANCE.enabledMinimapIcons.get(icon) == null) {
                    MapConfig.INSTANCE.enabledMinimapIcons.put(icon, e.getValue());
                }
            }

            MapConfig.INSTANCE.saveSettings(MapModule.getModule());

            enabled = (forMinimap ? MapConfig.INSTANCE.enabledMinimapIcons : MapConfig.INSTANCE.enabledMapIcons).get(translatedName);
            if (enabled == null) enabled = Boolean.FALSE;
        }

        return enabled;
    }

    public MapMarkerProfile getMapMarkerProfile() {
        return mmp;
    }

    // As an optimisation, so methods don't need to be called all the time
    @Override
    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier, float blockScale) {
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
        if (Mappings.Map.map_icons_mappings == null) return;
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
        for (MapMarkerProfile mmp : WebManager.getApiMarkers()) {
            if (IGNORED_MARKERS.contains(mmp.getIcon())) continue;
            if (isApiMarkerValid(mmp, MapConfig.IconTexture.Classic)) classicApiMarkers.add(new MapApiIcon(mmp, MapConfig.IconTexture.Classic));
            if (isApiMarkerValid(mmp, MapConfig.IconTexture.Medieval)) medievalApiMarkers.add(new MapApiIcon(mmp, MapConfig.IconTexture.Medieval));
        }
    }

    public static void resetApiMarkers(MapConfig.IconTexture iconTexture) {
        if (Mappings.Map.map_icons_mappings == null) return;
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
        for (MapMarkerProfile mmp : WebManager.getApiMarkers()) {
            if (IGNORED_MARKERS.contains(mmp.getIcon())) continue;
            if (isApiMarkerValid(mmp, iconTexture)) markers.add(new MapApiIcon(mmp, iconTexture));
        }
    }

    public static List<MapIcon> getApiMarkers(MapConfig.IconTexture iconTexture) {
        if (Mappings.Map.map_icons_mappings == null) return Collections.emptyList();
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
        boolean valid = Mappings.Map.map_icons_mappings.get(iconTexture == MapConfig.IconTexture.Classic ? "CLASSIC" : "MEDIEVAL").getAsJsonObject().has(mmp.getIcon());
        if (!valid && Reference.developmentEnvironment) {
            Reference.LOGGER.warn("No " + iconTexture + " texture for \"" + mmp.getIcon() + "\"");
        }
        return valid;
    }

    /**
     * Return a MapApiIcon that can render a map marker being free from position information
     */
    public static MapApiIcon getFree(String icon, MapConfig.IconTexture iconTexture) {
        icon = MAPMARKERNAME_REVERSE_TRANSLATION.getOrDefault(icon, icon);
        if (!MAPMARKERNAME_TRANSLATION.containsKey(icon)) {
            throw new RuntimeException("MapWaypointIcon.getFree(\"" + icon + "\"): invalid name");
        }
        return new MapApiIcon(new MapMarkerProfile(null, NO_LOCATION, NO_LOCATION, NO_LOCATION, icon), iconTexture);
    }

}
