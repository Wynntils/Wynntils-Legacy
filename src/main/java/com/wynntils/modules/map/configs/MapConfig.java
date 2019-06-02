/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.configs;

import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.map.instances.WaypointProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SettingsInfo(name = "map", displayPath = "Map")
public class MapConfig extends SettingsClass {
    public static MapConfig INSTANCE;

    @Setting(displayName = "Enable Minimap", description = "Should a minimap be displayed?", order = 0)
    public boolean enabled = true;

    @Setting(displayName = "Minimap Shape", description = "Should the minimap be a square or a circle?", order = 2)
    public MapFormat mapFormat = MapFormat.CIRCLE;

    @Setting(displayName = "Minimap Rotation", description = "Should the minimap be locked facing north or rotate based on the direction you're facing?", order = 3)
    public boolean followPlayerRotation = true;

    @Setting(displayName = "Show Compass Directions", description = "Should the cardinal directions (N, E, S, W) been displayed on the minimap", order = 4)
    public boolean showCompass = true;

    @Setting(displayName = "Minimap Size", description = "How large should the minimap be?", order = 1)
    @Setting.Limitations.IntLimit(min = 75, max = 200)
    public int mapSize = 100;

    @Setting(displayName = "Display Only North", description = "Should only north be displayed on the minimap?", order = 5)
    public boolean northOnly = false;

    @Setting(displayName = "Minimap Zoom", description = "How far zoomed out should the minimap be?")
    @Setting.Limitations.IntLimit(min = 0, max = 100, precision = 5)
    public int mapZoom = 30;

    public HashMap<String, Boolean> enabledMapIcons = resetMapIcons();

    @SettingsInfo(name = "map_worldmap", displayPath = "Map/World Map")
    public static class WorldMap extends SettingsClass {
        public static WorldMap INSTANCE;

        @Setting(displayName = "Keep Territory Visible", description = "Should territory names always be rendered rather than displaying only when you hold CTRL?")
        public boolean keepTerritoryVisible = false;

        @Setting(displayName = "Territory Names", description = "Should territory names be displayed?")
        public boolean showTerritoryName = false;

        @Setting(displayName = "Territory Limit", description = "Should territories loading be limited to the size of your screen?\n\n§8Disabling this can cause massive lag while the map is open.")
        public boolean limitTerritories = true;

    }

    @SettingsInfo(name = "map_textures", displayPath = "Map/Textures")
    public static class Textures extends SettingsClass {
        public static Textures INSTANCE;

        @Setting(displayName = "Minimap Texture Style", description = "What should the texture of the minimap be?", order = 0)
        public TextureType textureType = TextureType.Paper;

        @Setting(displayName = "Pointer Style", description = "What should the texture of the pointer be?" ,order = 1)
        public PointerType pointerStyle = PointerType.ARROW;

        @Setting(displayName = "Pointer Colour", description = "What should the colour of the pointer be?\n\n§aClick the coloured box to open the colour wheel.", order = 2)
        public CustomColor pointerColor = new CustomColor(1, 1, 1, 1);

    }

    @SettingsInfo(name = "waypoints", displayPath = "Map/Waypoints")
    public static class Waypoints extends SettingsClass {
        public static Waypoints INSTANCE;

        //HeyZeer0: this stores all waypoints
        @Setting(upload = true)
        public ArrayList<WaypointProfile> waypoints = new ArrayList<>();


        @Setting(displayName = "Recording Chest Waypoints", description = "Which chest tiers should be recorded as waypoints?\n\n§8Tiers higher than the specified value will also be recorded.", order = 6)
        public ChestTiers chestTiers = ChestTiers.TIER_3;

        public enum ChestTiers {
            TIER_1(4),
            TIER_2(3),
            TIER_3(2),
            TIER_4(1),
            NONE(0);

            private int tierArrayIndex; //Array starts at 1 :P
            private String[] tiers = new String[]{"IV", "III", "II", "I"};

            ChestTiers(int tierArrayIndex) {
                this.tierArrayIndex = tierArrayIndex;
            }

            public boolean isTierAboveThis(String testTier) {
                ArrayList<String> allowedTiers = new ArrayList<String>(Arrays.asList(Arrays.copyOfRange(tiers, 0, tierArrayIndex)));
                return allowedTiers.contains(testTier);
            }
        }

        @Setting(displayName = "Compass Marker", description = "Should a marker appear on the map where the compass is currently pointing towards?")
        public boolean compassMarker = true;
    }


    public enum MapFormat {
        SQUARE, CIRCLE
    }

    public enum TextureType {
        Paper, Wynn, Gilded
    }

    public enum PointerType {

        ARROW(10, 8, 5, 4, 0), CURSOR(8, 7, 4, 3.5f, 8), NARROW(8, 8, 4, 4, 15), ROUND(8, 8, 4, 4, 23), STRAIGHT(6, 8, 3, 4, 31), TRIANGLE(8, 6, 4, 3, 39);

        public float width, height, dWidth, dHeight, yStart;

        PointerType(float width, float height, float dWidth, float dHeight, float yStart) {
            this.width = width; this.height = height; this.dWidth = dWidth; this.dHeight = dHeight; this.yStart = yStart;
        }
    }

    @Override
    public void onSettingChanged(String name) { }

    public HashMap<String, Boolean> resetMapIcons() {
        return new HashMap<String, Boolean>() {{
            put("Content_Dungeon", true);
            put("Merchant_Accessory", true);
            put("Merchant_Armour", true);
            put("Merchant_Dungeon", true);
            put("Merchant_Horse", true);
            put("Merchant_KeyForge", true);
            put("Merchant_Liquid", true);
            put("Merchant_Potion", true);
            put("Merchant_Powder", true);
            put("Merchant_Scroll", true);
            put("Merchant_Seasail", true);
            put("Merchant_Weapon", true);
            put("NPC_Blacksmith", true);
            put("NPC_GuildMaster", true);
            put("NPC_ItemIdentifier", true);
            put("NPC_PowderMaster", true);
            put("Special_FastTravel", true);
            put("tnt", true);
            put("painting", true);
            put("Ore_Refinery", true);
            put("Fish_Refinery", true);
            put("Wood_Refinery", true);
            put("Crop_Refinery", true);
            put("MarketPlace", true);
            put("Content_Quest", false);
            put("Special_Rune", false);
            put("Special_RootsOfCorruption", true);
            put("Content_UltimateDiscovery", false);
            put("Content_Cave", false);
            put("Content_GrindSpot", false);
            put("Merchant_Other", false);
            put("Special_LightRealm", true);
            put("Merchant_Emerald", true);
        }};
    }

    public IconTexture iconTexture = IconTexture.Classic;
    public enum IconTexture {
        Classic, Medieval
    }

}
