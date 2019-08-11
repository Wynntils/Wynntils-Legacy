/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.ModCore;
import com.wynntils.core.framework.instances.KeyHolder;
import com.wynntils.core.framework.settings.ui.SettingsUI;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.modules.core.CoreModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.overlays.hud.StopWatchOverlay;
import com.wynntils.webapi.WebManager;
import org.lwjgl.input.Keyboard;

public class KeyManager {

    private static float lastGamma = 1f;

    private static KeyHolder lockInventoryKey;
    private static KeyHolder checkForUpdatesKey;
    private static KeyHolder zoomInKey;
    private static KeyHolder zoomOutKey;
    private static KeyHolder stopwatchKey;

    public static void registerKeys() {
        UtilitiesModule.getModule().registerKeyBinding("Gammabright", Keyboard.KEY_G, "Wynntils", true, () -> {
            if(ModCore.mc().gameSettings.gammaSetting < 1000) {
                lastGamma = ModCore.mc().gameSettings.gammaSetting;
                ModCore.mc().gameSettings.gammaSetting = 1000;
            }else{
                ModCore.mc().gameSettings.gammaSetting = lastGamma;
            }
        });

        checkForUpdatesKey = CoreModule.getModule().registerKeyBinding("Check for Updates", Keyboard.KEY_L, "Wynntils", true, WebManager::checkForUpdates);

        CoreModule.getModule().registerKeyBinding("Open Settings", Keyboard.KEY_P, "Wynntils", true, () -> {
            SettingsUI ui = new SettingsUI(ModCore.mc().currentScreen);
            UI.setupUI(ui);
            ModCore.mc().displayGuiScreen(ui);
        });

        lockInventoryKey = UtilitiesModule.getModule().registerKeyBinding("Lock Slot", Keyboard.KEY_H, "Wynntils", true, () -> {});

        zoomInKey = CoreModule.getModule().registerKeyBinding("Zoom In", Keyboard.KEY_EQUALS, "Wynntils", true, () -> {
            if (MapConfig.INSTANCE.mapZoom >= 5) {
                MapConfig.INSTANCE.mapZoom -= 5;
            }
        });

        zoomOutKey = CoreModule.getModule().registerKeyBinding("Zoom Out", Keyboard.KEY_MINUS, "Wynntils", true, () -> {
            if (MapConfig.INSTANCE.mapZoom <= 95) {
                MapConfig.INSTANCE.mapZoom += 5;
            }
        });

        CoreModule.getModule().registerKeyBinding("Cast First Spell", Keyboard.KEY_Z, "Wynntils", true, QuickCastManager::castFirstSpell);
        CoreModule.getModule().registerKeyBinding("Cast Second Spell", Keyboard.KEY_X, "Wynntils", true, QuickCastManager::castSecondSpell);
        CoreModule.getModule().registerKeyBinding("Cast Third Spell", Keyboard.KEY_C, "Wynntils", true, QuickCastManager::castThirdSpell);
        CoreModule.getModule().registerKeyBinding("Cast Fourth Spell", Keyboard.KEY_V, "Wynntils", true, QuickCastManager::castFourthSpell);

        CoreModule.getModule().registerKeyBinding("Mount Horse", Keyboard.KEY_Y, "Wynntils", true, MountHorseManager::mountHorseAndShowMessage);

        stopwatchKey = CoreModule.getModule().registerKeyBinding("Start/Stop StopWatch", Keyboard.KEY_NUMPAD5, "Wynntils", true, StopWatchOverlay::start);
    }

    public static KeyHolder getLockInventoryKey() {
        return lockInventoryKey;
    }

    public static KeyHolder getCheckForUpdatesKey() {
        return checkForUpdatesKey;
    }

    public static KeyHolder getZoomInKey() {
        return zoomInKey;
    }

    public static KeyHolder getZoomOutKey() {
        return zoomOutKey;
    }

    public static KeyHolder getStopwatchKey() {
        return stopwatchKey;
    }

}
