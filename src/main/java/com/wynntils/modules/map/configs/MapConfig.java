/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.configs;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.overlays.objects.MapWaypointIcon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SettingsInfo(name = "map", displayPath = "Map")
public class MapConfig extends SettingsClass {
    public static MapConfig INSTANCE;

    @Setting(displayName = "Show Compass Beam", description = "Should a beacon beam be displayed at your compass position?", order = 0)
    public boolean showCompassBeam = true;

    @Setting(displayName = "Show Compass Directions", description = "Should the cardinal directions (N, E, S, W) been displayed on the minimap", order = 1)
    public boolean showCompass = true;

    @Setting(displayName = "Enable Minimap", description = "Should a minimap be displayed?", order = 2)
    public boolean enabled = true;

    @Setting(displayName = "Minimap Shape", description = "Should the minimap be a square or a circle?", order = 3)
    public MapFormat mapFormat = MapFormat.CIRCLE;

    @Setting(displayName = "Minimap Rotation", description = "Should the minimap be locked facing north or rotate based on the direction you're facing?", order = 4)
    public boolean followPlayerRotation = true;

    @Setting(displayName = "Minimap Size", description = "How large should the minimap be?", order = 5)
    @Setting.Limitations.IntLimit(min = 75, max = 200)
    public int mapSize = 100;

    @Setting(displayName = "Minimap Coordinates", description = "Should your coordinates be displayed below the minimap?", order = 6)
    public boolean showCoords = false;

    @Setting(displayName = "Display Only North", description = "Should only north be displayed on the minimap?", order = 7)
    public boolean northOnly = false;

    @Setting(displayName = "Display Minimap Icons", description = "Should map icons be displayed on the minimap?", order = 8)
    public boolean minimapIcons = true;

    @Setting(displayName = "Minimap Icons Size", description = "How big should minimap icons be?", order = 9)
    @Setting.Limitations.FloatLimit(min = 0.5f, max = 2f)
    public float minimapIconSizeMultiplier = 1f;

    @Setting(displayName = "Minimap Zoom", description = "How far zoomed out should the minimap be?")
    @Setting.Limitations.IntLimit(min = 0, max = 100, precision = 5)
    public int mapZoom = 30;

    public HashMap<String, Boolean> enabledMapIcons = resetMapIcons();

    @SettingsInfo(name = "map_worldmap", displayPath = "Map/World Map")
    public static class WorldMap extends SettingsClass {
        public static WorldMap INSTANCE;

        @Setting(displayName = "Keep Territory Visible", description = "Should territory names always be displayed rather than only when you hold CTRL?")
        public boolean keepTerritoryVisible = false;

        @Setting(displayName = "Territory Names", description = "Should territory names be displayed?")
        public boolean showTerritoryName = false;

        @Setting(displayName = "Territory Guild Tags", description = "Should be guild names be replaced by their guild tags?")
        public boolean useGuildShortNames = true;

        @Setting(displayName = "Territory Colour Transparency", description = "How transparent should the colour of territories be?")
        @Setting.Limitations.FloatLimit(min = 0.1f, max = 1f)
        public float colorAlpha = 0.4f;

        @Setting(displayName = "Show Territory Areas", description = "Should territory rectangles be visible?")
        public boolean territoryArea = true;

        // If this ever needs to be configurable, make these into @Setting s.
        public int maxZoom = 150;  // Note that this is the most zoomed out
        public int minZoom = -10;  // And this is the most zoomed in
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

        @Override
        public void saveSettings(Module m) {
            super.saveSettings(m);
            MapWaypointIcon.resetWaypoints();
        }

        @Override
        public void onSettingChanged(String name) {
            MapWaypointIcon.resetWaypoints();
        }
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
            put("Dungeons", true);
            put("Accessory Merchant", true);
            put("Armour Merchant", true);
            put("Dungeon Merchant", true);
            put("Horse Merchant", true);
            put("Key Forge Merchant", true);
            put("LE Merchant", true);
            put("Emerald Merchant", true);
            put("TNT Merchant", true);
            put("Ore Refinery", true);
            put("Potion Merchant", true);
            put("Powder Merchant", true);
            put("Scroll Merchant", true);
            put("Seasail Merchant", true);
            put("Weapon Merchant", true);
            put("Blacksmith", true);
            put("Guild Master", true);
            put("Item Identifier", true);
            put("Powder Master", true);
            put("Fast Travel", true);
            put("Fish Refinery", true);
            put("Wood Refinery", true);
            put("Crop Refinery", true);
            put("Marketplace", true);
            put("Quests", false);
            put("Runes", false);
            put("Nether Portal", true);
            put("Ultimate Discovery", false);
            put("Caves", false);
            put("Grind Spots", false);
            put("Other Merchants", false);
            put("Light's Secret", true);

        }};
    }

    public IconTexture iconTexture = IconTexture.Classic;
    public enum IconTexture {
        Classic, Medieval
    }

}
