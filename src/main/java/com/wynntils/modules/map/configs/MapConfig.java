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
import java.util.HashMap;

@SettingsInfo(name = "map", displayPath = "Map")
public class MapConfig extends SettingsClass {
    public static MapConfig INSTANCE;

    @Setting(displayName = "Enable Minimap", description = "Should a minimap be displayed?")
    public boolean enabled = true;

    @Setting(displayName = "Minimap Shape", description = "Should the minimap be a square or a circle?")
    public MapFormat mapFormat = MapFormat.CIRCLE;

    @Setting(displayName = "Minimap Rotation", description = "Should the minimap be locked facing north or rotate based on the direction you're facing?")
    public boolean followPlayerRotation = true;

    @Setting(displayName = "Show Compass Directions", description = "Should the cardinal directions (N, E, S, W) been displayed on the minimap")
    public boolean showCompass = true;

    @Setting(displayName = "Minimap Size", description = "How large should the minimap be?")
    @Setting.Limitations.IntLimit(min = 75, max = 200)
    public int mapSize = 100;

    @Setting(displayName = "Display Only North", description = "Should only north be displayed on the minimap?")
    public boolean northOnly = false;

    @Setting(displayName = "Minimap Zoom", description = "How far zoomed out should the minimap be?")
    @Setting.Limitations.IntLimit(min = 0, max = 100, precision = 5)
    public int mapZoom = 30;

    public HashMap<String, Boolean> enabledMapIcons = resetMapIcons();

    @SettingsInfo(name = "map_textures", displayPath = "Map/Textures")
    public static class Textures extends SettingsClass {
        public static Textures INSTANCE;

        @Setting(displayName = "MiniMap Texture Style", description = "What should the texture of the minimap be?")
        public TextureType textureType = TextureType.Paper;

        @Setting(displayName = "Pointer Style", description = "What should the texture of the pointer be?")
        public PointerType pointerStyle = PointerType.ARROW;

        @Setting(displayName = "Pointer Color", description = "What should the colour of the pointer be?\n\n§aClick above the color to open the color wheel")
        public CustomColor pointerColor = new CustomColor(1, 1, 1, 1);

    }

    @SettingsInfo(name = "waypoints", displayPath = "Map/Waypoints")
    public static class Waypoints extends SettingsClass {
        public static Waypoints INSTANCE;

        //HeyZeer0: this stores all waypoints
        public ArrayList<WaypointProfile> waypoints = new ArrayList<>();

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
        Classic, Medival
    }
}
