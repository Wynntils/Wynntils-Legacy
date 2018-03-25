package cf.wynntils.modules.utilities;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import cf.wynntils.modules.utilities.configs.UtilitiesDataConfig;
import cf.wynntils.modules.utilities.events.CommonEvents;
import cf.wynntils.modules.utilities.managers.KeyManager;
import cf.wynntils.modules.utilities.overlays.OverlayEvents;
import cf.wynntils.modules.utilities.overlays.hud.*;
import org.lwjgl.input.Keyboard;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@ModuleInfo(name = "Utilities")
public class UtilitiesModule extends Module {

    private static UtilitiesModule module;

    private static UtilitiesConfig mainConfig;
    private static UtilitiesDataConfig dataConfig;

    public void onEnable() {
        module = this;

        KeyManager.registerKeys();

        registerEvents(new OverlayEvents());
        registerEvents(new CommonEvents());

        registerOverlay(new HealthBarOverlay(), Priority.NORMAL);
        registerOverlay(new ManaBarOverlay(), Priority.NORMAL);
        registerOverlay(new ExpBarOverlay(), Priority.NORMAL);
        registerOverlay(new LevelingOverlay(), Priority.LOW);

        registerOverlay(new GammaOverlay(), Priority.NORMAL);
        registerOverlay(new LobbyCleanerOverlay(), Priority.LOW);

        registerOverlay(new DebugOverlay(),Priority.NORMAL);

        mainConfig = new UtilitiesConfig();
        registerSettings(mainConfig);
        dataConfig = new UtilitiesDataConfig();
        registerSettings(dataConfig);

        registerKeyBinding("test", Keyboard.KEY_K, "test", true, () -> {
            getMainConfig().saveSettings(this);
        });
    }

    public static UtilitiesModule getModule() {
        return module;
    }

    public static UtilitiesConfig getMainConfig() {
        return mainConfig;
    }

    public static UtilitiesDataConfig getData() {
        return dataConfig;
    }

}
