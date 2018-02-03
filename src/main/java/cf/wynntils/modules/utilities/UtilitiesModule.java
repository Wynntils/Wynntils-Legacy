package cf.wynntils.modules.utilities;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.utilities.managers.KeyManager;
import cf.wynntils.modules.utilities.overlays.OverlayEvents;
import cf.wynntils.modules.utilities.overlays.hud.ActionBarOverlay;
import cf.wynntils.modules.utilities.overlays.hud.GammaOverlay;
import cf.wynntils.modules.utilities.overlays.hud.HealthOverlay;
import cf.wynntils.modules.utilities.overlays.hud.ManaOverlay;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@ModuleInfo(name = "Utilities")
public class UtilitiesModule extends Module {

    private static UtilitiesModule module;

    public void onEnable() {
        module = this;

        KeyManager.registerKeys();

        registerEvents(new OverlayEvents());

        registerHudOverlay(new ActionBarOverlay(getMinecraft(), 0, 0));
        registerHudOverlay(new HealthOverlay(getMinecraft(), 0, 0));
        registerHudOverlay(new ManaOverlay(getMinecraft(), 0, 0));
        registerHudOverlay(new GammaOverlay(getMinecraft(), 70, 5));
    }

    public static UtilitiesModule getModule() {
        return module;
    }

}
