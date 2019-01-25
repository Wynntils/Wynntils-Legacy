package cf.wynntils.modules.map;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.map.configs.MapConfig;
import cf.wynntils.modules.map.instances.MapProfile;
import cf.wynntils.modules.map.overlays.MiniMapOverlay;
import cf.wynntils.modules.map.overlays.ui.WorldMapOverlay;
import cf.wynntils.webapi.WebManager;
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
