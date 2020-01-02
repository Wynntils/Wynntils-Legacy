/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.configs;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.map.instances.PathWaypointProfile;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.overlays.objects.MapPathWaypointIcon;
import com.wynntils.modules.map.overlays.objects.MapWaypointIcon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SettingsInfo(name = "map", displayPath = "wynntils.config.map.display_path")
public class MapConfig extends SettingsClass {
    public static MapConfig INSTANCE;

    @Setting(displayName = "wynntils.config.map.compass_beam.display_name", description = "wynntils.config.map.compass_beam.description", order = 0)
    public boolean showCompassBeam = true;

    @Setting(displayName = "wynntils.config.map.compass_directions.display_name", description = "wynntils.config.map.compass_directions.description", order = 4)
    public boolean showCompass = true;

    @Setting(displayName = "wynntils.config.map.enable_minimap.display_name", description = "wynntils.config.map.enable_minimap.description", order = 2)
    public boolean enabled = true;

    @Setting(displayName = "wynntils.config.map.minimap_shape.display_name", description = "wynntils.config.map.minimap_shape.description", order = 3)
    public MapFormat mapFormat = MapFormat.CIRCLE;

    @Setting(displayName = "wynntils.config.map.minimap_rotation.display_name", description = "wynntils.config.map.minimap_rotation.description", order = 4)
    public boolean followPlayerRotation = true;

    @Setting(displayName = "wynntils.config.map.minimap_size.display_name", description = "wynntils.config.map.minimap_size.description", order = 5)
    @Setting.Limitations.IntLimit(min = 75, max = 200)
    public int mapSize = 100;

    @Setting(displayName = "wynntils.config.map.minimap_coords.display_name", description = "wynntils.config.map.minimap_coords.description", order = 6)
    public boolean showCoords = false;

    @Setting(displayName = "wynntils.config.map.only_north.display_name", description = "wynntils.config.map.only_north.description", order = 7)
    public boolean northOnly = false;

    @Setting(displayName = "wynntils.config.map.minimap_icons.display_name", description = "wynntils.config.map.minimap_icons.description", order = 8)
    public boolean minimapIcons = true;

    @Setting(displayName = "wynntils.config.map.minimap_icons_size.display_name", description = "wynntils.config.map.minimap_icons_size.description", order = 9)
    @Setting.Limitations.FloatLimit(min = 0.5f, max = 2f)
    public float minimapIconSizeMultiplier = 1f;

    @Setting(displayName = "wynntils.config.map.minimap_zoom.display_name", description = "wynntils.config.map.minimap_zoom.description")
    @Setting.Limitations.IntLimit(min = 0, max = 100, precision = 5)
    public int mapZoom = 30;

    public HashMap<String, Boolean> enabledMapIcons = resetMapIcons(false);
    public HashMap<String, Boolean> enabledMinimapIcons = resetMapIcons(true);

    @SettingsInfo(name = "map_worldmap", displayPath = "wynntils.config.map.world_map.display_path")
    public static class WorldMap extends SettingsClass {
        public static WorldMap INSTANCE;

        @Setting(displayName = "wynntils.config.map.world_map.territory_visible.display_name", description = "wynntils.config.map.world_map.territory_visible.description")
        public boolean keepTerritoryVisible = false;

        @Setting(displayName = "wynntils.config.map.world_map.territory_names.display_name", description = "wynntils.config.map.world_map.territory_names.description")
        public boolean showTerritoryName = false;

        @Setting(displayName = "wynntils.config.map.world_map.territory_guild_tags.display_name", description = "wynntils.config.map.world_map.territory_guild_tags.description")
        public boolean useGuildShortNames = true;

        @Setting(displayName = "wynntils.config.map.world_map.territory_color_transparency.display_name", description = "wynntils.config.map.world_map.territory_color_transparency.description")
        @Setting.Limitations.FloatLimit(min = 0.1f, max = 1f)
        public float colorAlpha = 0.4f;

        @Setting(displayName = "wynntils.config.map.world_map.territory_area.display_name", description = "wynntils.config.map.world_map.territory_area.description")
        public boolean territoryArea = true;
    }

    @SettingsInfo(name = "map_textures", displayPath = "wynntils.config.map.textures.display_path")
    public static class Textures extends SettingsClass {
        public static Textures INSTANCE;

        @Setting(displayName = "wynntils.config.map.textures.minimap_style.display_name", description = "wynntils.config.map.textures.minimap_style.description", order = 0)
        public TextureType textureType = TextureType.Paper;

        @Setting(displayName = "wynntils.config.map.textures.pointer_style.display_name", description = "wynntils.config.map.textures.pointer_style.description" ,order = 1)
        public PointerType pointerStyle = PointerType.ARROW;

        @Setting(displayName = "wynntils.config.map.textures.pointer_color.display_name", description = "wynntils.config.map.textures.pointer_color.description", order = 2)
        public CustomColor pointerColor = new CustomColor(1, 1, 1, 1);

    }

    @SettingsInfo(name = "waypoints", displayPath = "wynntils.config.map.waypoints.display_path")
    public static class Waypoints extends SettingsClass {
        public static Waypoints INSTANCE;

        //HeyZeer0: this stores all waypoints
        @Setting(upload = true)
        public ArrayList<WaypointProfile> waypoints = new ArrayList<>();

        @Setting(upload = true)
        public ArrayList<PathWaypointProfile> pathWaypoints = new ArrayList<>();

        @Setting(displayName = "wynntils.config.map.record_chest_tier.display_name", description = "wynntils.config.map.record_chest_tier.description", order = 6)
        public ChestTiers chestTiers = ChestTiers.TIER_3;

        public enum ChestTiers {
            TIER_1(4, "wynntils.config.map.enum.chest_tier.tier_1"),
            TIER_2(3, "wynntils.config.map.enum.chest_tier.tier_2"),
            TIER_3(2, "wynntils.config.map.enum.chest_tier.tier_3"),
            TIER_4(1, "wynntils.config.map.enum.chest_tier.tier_4"),
            NONE(0, "wynntils.config.map.enum.chest_tier.none");

            private int tierArrayIndex; //Array starts at 1 :P
            private String[] tiers = new String[]{"IV", "III", "II", "I"};
            public String displayName;

            ChestTiers(int tierArrayIndex, String displayName) {
                this.tierArrayIndex = tierArrayIndex;
                this.displayName = displayName;
            }

            public boolean isTierAboveThis(String testTier) {
                ArrayList<String> allowedTiers = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(tiers, 0, tierArrayIndex)));
                return allowedTiers.contains(testTier);
            }
        }

        @Setting(displayName = "wynntils.config.map.waypoints.compass_marker.display_name", description = "wynntils.config.map.waypoints.compass_marker.description")
        public boolean compassMarker = true;

        @Override
        public void saveSettings(Module m) {
            super.saveSettings(m);
            MapWaypointIcon.resetWaypoints();
            MapPathWaypointIcon.resetPathWaypoints();
        }

        @Override
        public void onSettingChanged(String name) {
            super.onSettingChanged(name);
            MapWaypointIcon.resetWaypoints();
            MapPathWaypointIcon.resetPathWaypoints();
        }
    }


    public enum MapFormat {
        SQUARE("wynntils.config.map.enum.map_format.square"),
        CIRCLE("wynntils.config.map.enum.map_format.circle");

        public String displayName;

        MapFormat(String displayName) {
            this.displayName = displayName;
        }
    }

    public enum TextureType {
        Paper("wynntils.config.map.textures.enum.texture_type.paper"),
        Wynn("wynntils.config.map.textures.enum.texture_type.wynn"),
        Gilded("wynntils.config.map.textures.enum.texture_type.gilded");

        public String displayName;

        TextureType(String displayName) {
            this.displayName = displayName;
        }
    }

    public enum PointerType {

        ARROW(10, 8, 5, 4, 0, "wynntils.config.map.textures.enum.pointer_type.arrow"),
        CURSOR(8, 7, 4, 3.5f, 8, "wynntils.config.map.textures.enum.pointer_type.cursor"),
        NARROW(8, 8, 4, 4, 15, "wynntils.config.map.textures.enum.pointer_type.narrow"),
        ROUND(8, 8, 4, 4, 23, "wynntils.config.map.textures.enum.pointer_type.round"),
        STRAIGHT(6, 8, 3, 4, 31, "wynntils.config.map.textures.enum.pointer_type.straight"),
        TRIANGLE(8, 6, 4, 3, 39, "wynntils.config.map.textures.enum.pointer_type.triangle");

        public float width, height, dWidth, dHeight, yStart;
        public String displayName;

        PointerType(float width, float height, float dWidth, float dHeight, float yStart, String displayName) {
            this.width = width; this.height = height; this.dWidth = dWidth; this.dHeight = dHeight; this.yStart = yStart; this.displayName = displayName;
        }
    }

    @Override
    public void onSettingChanged(String name) { }

    public HashMap<String, Boolean> resetMapIcons(boolean forMiniMap) {
        HashMap<String, Boolean> enabledIcons = new HashMap<>();
        for (String icon : new String[]{
            "Dungeons", "Accessory Merchant", "Armour Merchant", "Dungeon Merchant", "Horse Merchant",
            "Key Forge Merchant", "LE Merchant", "Emerald Merchant", "TNT Merchant", "Ore Refinery",
            "Potion Merchant", "Powder Merchant", "Scroll Merchant", "Seasail Merchant", "Weapon Merchant",
            "Blacksmith", "Guild Master", "Item Identifier", "Powder Master", "Fast Travel",
            "Fish Refinery", "Wood Refinery", "Crop Refinery", "Marketplace", "Nether Portal",
            "Light's Secret"
        }) {
            enabledIcons.put(icon, true);
        }
        for (String icon : new String[]{
            "Quests", "Runes", "Ultimate Discovery", "Caves", "Grind Spots", "Other Merchants"
        }) {
            enabledIcons.put(icon, forMiniMap);
        }
        return enabledIcons;
    }

    public IconTexture iconTexture = IconTexture.Classic;
    public enum IconTexture {
        Classic("wynntils.map.enum.icon_texutre.classic"),
        Medieval("wynntils.map.enum.icon_texutre.medieval");

        public String displayName;

        IconTexture(String displayName) {
            this.displayName = displayName;
        }
    }
}
