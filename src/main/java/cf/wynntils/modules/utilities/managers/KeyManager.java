package cf.wynntils.modules.utilities.managers;

import cf.wynntils.ModCore;
import cf.wynntils.core.framework.instances.KeyHolder;
import cf.wynntils.core.framework.settings.ui.SettingsUI;
import cf.wynntils.core.framework.ui.UI;
import cf.wynntils.modules.core.CoreModule;
import cf.wynntils.modules.map.configs.MapConfig;
import cf.wynntils.modules.utilities.UtilitiesModule;
import cf.wynntils.webapi.WebManager;
import org.lwjgl.input.Keyboard;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class KeyManager {

    private static float lastGamma = 1f;

    private static KeyHolder lockInventoryKey;
    private static KeyHolder checkForUpdatesKey;

    public static void registerKeys() {
        UtilitiesModule.getModule().registerKeyBinding("Gammabright", Keyboard.KEY_G, "Wynntils", true, () -> {
            if(ModCore.mc().gameSettings.gammaSetting < 1000) {
                lastGamma = ModCore.mc().gameSettings.gammaSetting;
                ModCore.mc().gameSettings.gammaSetting = 1000;
            }else{
                ModCore.mc().gameSettings.gammaSetting = lastGamma;
            }
        });

        checkForUpdatesKey = CoreModule.getModule().registerKeyBinding("Check for updates", Keyboard.KEY_L, "Wynntils", true, WebManager::checkForUpdates);

        CoreModule.getModule().registerKeyBinding("Open Settings", Keyboard.KEY_P, "Wynntils", true, () -> {
            SettingsUI ui = new SettingsUI(ModCore.mc().currentScreen);
            UI.setupUI(ui);
            ModCore.mc().displayGuiScreen(ui);
        });

        CoreModule.getModule().registerKeyBinding("Refresh API", Keyboard.KEY_GRAVE, "Debug", true, () -> {
            WebManager.reset();
            WebManager.setupWebApi();
        });

        UtilitiesModule.getModule().registerKeyBinding("Debug Key", Keyboard.KEY_J, "Debug", true, () -> { });

        lockInventoryKey = UtilitiesModule.getModule().registerKeyBinding("Lock Slot", Keyboard.KEY_H, "Wynntils", true, () -> {});

        CoreModule.getModule().registerKeyBinding("Zoom in", Keyboard.KEY_EQUALS, "Wynntils", true, () -> {
            if (MapConfig.INSTANCE.mapZoom <= 95) {
                MapConfig.INSTANCE.mapZoom += 5;
            }
        });

        CoreModule.getModule().registerKeyBinding("Zoom out", Keyboard.KEY_MINUS, "Wynntils", true, () -> {
            if (MapConfig.INSTANCE.mapZoom >= 5) {
                MapConfig.INSTANCE.mapZoom -= 5;
            }
        });
    }

    public static KeyHolder getLockInventoryKey() {
        return lockInventoryKey;
    }

    public static KeyHolder getCheckForUpdatesKey() {
        return checkForUpdatesKey;
    }
}
