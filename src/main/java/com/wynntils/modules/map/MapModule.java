/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.map;

import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.overlays.MiniMapOverlay;
import com.wynntils.modules.map.overlays.ui.WorldMapOverlay;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

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

        registerKeyBinding("Open Map", Keyboard.KEY_M, "Wynntils", true, () -> Minecraft.getMinecraft().displayGuiScreen(new WorldMapOverlay()));
    }

    public static MapModule getModule() {
        return module;
    }

    public MapProfile getMainMap() {
        return mainMap;
    }

}
