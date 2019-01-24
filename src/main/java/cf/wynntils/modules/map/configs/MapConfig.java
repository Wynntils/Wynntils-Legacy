package cf.wynntils.modules.map.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "map", displayPath = "Map")
public class MapConfig extends SettingsClass {
    public static MapConfig INSTANCE;

    @Setting(displayName = "Map Format", description = "Should the Map be a Square or a Circle?")
    public MapFormat mapFormat = MapFormat.CIRCLE;

    @Setting(displayName = "Follow Player Rotation", description = "Should the Map Follow the Player's Rotation?")
    public boolean followPlayerRotation = true;

    @Setting(displayName = "Map Size", description = "How Large Should the Map be?")
    @Setting.Limitations.IntLimit(min = 100, max = 200, precision = 1)
    public int mapSize = 100;

    @Setting(displayName = "Texture Style", description = "What Should the Map Texture be?")
    public TextureType textureType = TextureType.Paper;

    public enum MapFormat {
        SQUARE, CIRCLE
    }

    public enum TextureType {
        Paper, Wynn
    }

    @Override
    public void onSettingChanged(String name) { }

}
