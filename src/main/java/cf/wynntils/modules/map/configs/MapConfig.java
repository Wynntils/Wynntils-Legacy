package cf.wynntils.modules.map.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "map", displayPath = "Map")
public class MapConfig extends SettingsClass {
    public static MapConfig INSTANCE;

    @Setting(displayName = "Map Format", description = "Should the map be a square or a circle")
    public MapFormat mapFormat = MapFormat.CIRCLE;

    @Setting(displayName = "Follow Player Rotation", description = "Should the map follow the player rotation")
    public boolean followPlayerRotation = true;

    @Setting(displayName = "Map Size", description = "What should be the map size")
    @Setting.Limitations.IntLimit(min = 100, max = 200, precision = 1)
    public int mapSize = 100;

    public enum MapFormat {
        SQUARE, CIRCLE
    }

    @Override
    public void onSettingChanged(String name) { }

}
