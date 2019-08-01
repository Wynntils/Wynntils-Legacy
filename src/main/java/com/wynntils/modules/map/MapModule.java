/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.KeyHolder;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.events.ClientEvents;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.MiniMapOverlay;
import com.wynntils.modules.map.overlays.ui.WorldMapUI;
import com.wynntils.webapi.WebManager;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "map", displayName = "Map")
public class MapModule extends Module {

    private static MapModule module;
    private static KeyHolder mapKey;
    private MapProfile mainMap;

    @Override
    public void onEnable() {
        module = this;

        mainMap = new MapProfile(WebManager.getApiUrls().get("MainMap"), "main-map");
        mainMap.updateMap();

        registerEvents(new ClientEvents());

        registerSettings(MapConfig.class);
        registerSettings(MapConfig.Textures.class);
        registerSettings(MapConfig.Waypoints.class);
        registerSettings(MapConfig.WorldMap.class);

        registerOverlay(new MiniMapOverlay(), Priority.LOWEST);

        mapKey = registerKeyBinding("Open Map", Keyboard.KEY_M, "Wynntils", true, () -> { if(Reference.onWorld) Utils.displayGuiScreen(new WorldMapUI()); });
    }

    public static MapModule getModule() {
        return module;
    }

    public MapProfile getMainMap() {
        return mainMap;
    }

    public KeyHolder getMapKey() { return mapKey; }
}
