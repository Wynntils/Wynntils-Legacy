/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

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

    public enum MapFormat {
        SQUARE, CIRCLE
    }

    public enum TextureType {
        Paper, Wynn, Gilded
    }

    @Override
    public void onSettingChanged(String name) { }

}
