package cf.wynntils.modules.utilities.managers;

import cf.wynntils.ModCore;
import cf.wynntils.modules.utilities.UtilitiesModule;
import org.lwjgl.input.Keyboard;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class KeyManager {

    private static float lastGamma = 1f;

    public static void registerKeys() {

        //Gammabright
        UtilitiesModule.getModule().registerKeyBinding("Gammabright", Keyboard.KEY_G, "Utilities", true, () -> {
            if(ModCore.mc().gameSettings.gammaSetting < 1000) {
                lastGamma = ModCore.mc().gameSettings.gammaSetting;
                ModCore.mc().gameSettings.gammaSetting = 1000;
            }else{
                ModCore.mc().gameSettings.gammaSetting = lastGamma;
            }
        });
    }

}
