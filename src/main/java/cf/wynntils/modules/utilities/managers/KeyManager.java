package cf.wynntils.modules.utilities.managers;

import cf.wynntils.ModCore;
import cf.wynntils.core.framework.settings.ui.SettingsUI;
import cf.wynntils.core.framework.ui.UI;
import cf.wynntils.modules.core.CoreModule;
import cf.wynntils.modules.utilities.UtilitiesModule;
import cf.wynntils.webapi.WebManager;
import org.lwjgl.input.Keyboard;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class KeyManager {

    private static float lastGamma = 1f;

    public static void registerKeys() {
        UtilitiesModule.getModule().registerKeyBinding("Gammabright", Keyboard.KEY_G, "Utilities", true, () -> {
            if(ModCore.mc().gameSettings.gammaSetting < 1000) {
                lastGamma = ModCore.mc().gameSettings.gammaSetting;
                ModCore.mc().gameSettings.gammaSetting = 1000;
            }else{
                ModCore.mc().gameSettings.gammaSetting = lastGamma;
            }
        });

        CoreModule.getModule().registerKeyBinding("Check for updates", Keyboard.KEY_L, "Wynntils", true, () -> {
            WebManager.checkForUpdates();
        });


        CoreModule.getModule().registerKeyBinding("Open Settings", Keyboard.KEY_P, "Wynntils", true, () -> {
            SettingsUI ui = new SettingsUI(ModCore.mc().currentScreen);
            UI.setupUI(ui);
            ModCore.mc().displayGuiScreen(ui);
        });

        CoreModule.getModule().registerKeyBinding("Refresh API", Keyboard.KEY_EQUALS, "Debug", true, () -> {
            WebManager.reset();
            WebManager.setupWebApi();
        });
    }

}
