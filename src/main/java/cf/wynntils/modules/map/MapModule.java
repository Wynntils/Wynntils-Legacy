package cf.wynntils.modules.map;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.map.configs.MapConfig;
import cf.wynntils.modules.map.instances.MapProfile;
import cf.wynntils.modules.map.overlays.MiniMapOverlay;
import cf.wynntils.webapi.WebManager;

@ModuleInfo(name = "map", displayName = "Map")
public class MapModule extends Module {

    private static MapModule module;

    private MapProfile mainMap;

    @Override
    public void onEnable() {
        module = this;

        mainMap = new MapProfile(WebManager.getApiUrls().get("MainMap"), "main-map");
        mainMap.updateMap();

        registerSettings(MapConfig.class);
        registerOverlay(new MiniMapOverlay(), Priority.LOWEST);
    }

    public static MapModule getModule() {
        return module;
    }

    public MapProfile getMainMap() {
        return mainMap;
    }

}
