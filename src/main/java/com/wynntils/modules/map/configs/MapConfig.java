/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

import java.util.HashMap;

@SettingsInfo(name = "map", displayPath = "Map")
public class MapConfig extends SettingsClass {
    public static MapConfig INSTANCE;

    @Setting(displayName = "Enable minimap", description = "Should a minimap be displayed?")
    public boolean enabled = true;

    @Setting(displayName = "Map Format", description = "Should the Map be a Square or a Circle?")
    public MapFormat mapFormat = MapFormat.CIRCLE;

    @Setting(displayName = "Follow Player Rotation", description = "Should the Map follow the Player's Rotation?")
    public boolean followPlayerRotation = true;

    @Setting(displayName = "Show Compass Directions", description = "Should compass directions (N, S, E, W) been shown on the map")
    public boolean showCompass = true;

    @Setting(displayName = "Map Size", description = "How large should the Map be?")
    @Setting.Limitations.IntLimit(min = 75, max = 200)
    public int mapSize = 100;

    @Setting(displayName = "North only", description = "Should only the cardinal direction: North, be displayed?")
    public boolean northOnly = false;

    @Setting(displayName = "Map Zoom", description = "How far zoomed out should the map be?")
    @Setting.Limitations.IntLimit(min = 0, max = 100, precision = 5)
    public int mapZoom = 30;

    @Setting(displayName = "Texture Style", description = "What should the Map Texture be?")
    public TextureType textureType = TextureType.Paper;

    @Setting(displayName = "Pointer Style", description = "What should the pointer texture style be?")
    public PointerType pointerStyle = PointerType.ARROW;

    @Setting(displayName = "Pointer Color", description = "What should the pointer color be?")
    public PointerColor pointerColor = PointerColor.RED;

    public HashMap<String, Boolean> enabledMapIcons = new HashMap<String, Boolean>() {{
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
        put("Content_Quest", true);
        put("Special_Rune", true);
        put("Content_UltimateDiscovery", true);
        put("Content_Cave", true);
        put("Content_GrindSpot", true);
        put("Merchant_Other", true);
        put("Special_LightRealm", true);
        put("Merchant_Emerald", true);
    }};

    public enum MapFormat {
        SQUARE, CIRCLE
    }

    public enum TextureType {
        Paper, Wynn, Gilded
    }

    public enum PointerType {

        ARROW(10, 8, 5, 4, 0), PIN(8, 10, 4, 5, 32);

        public int width, height, dWidth, dHeight, yStart;

        PointerType(int width, int height, int dWidth, int dHeight, int yStart) {
            this.width = width; this.height = height; this.dWidth = dWidth; this.dHeight = dHeight; this.yStart = yStart;
        }
    }

    public enum PointerColor {
        BLUE(0), RED(1), WHITE(2), YELLOW(3);

        public int index;

        PointerColor(int index) {
            this.index = index;
        }
    }

    @Override
    public void onSettingChanged(String name) { }

}
