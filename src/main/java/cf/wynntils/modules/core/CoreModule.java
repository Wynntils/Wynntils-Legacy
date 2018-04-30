package cf.wynntils.modules.core;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.core.config.CoreDBConfig;
import cf.wynntils.modules.core.events.ServerEvents;
import cf.wynntils.modules.core.overlays.DownloadOverlay;
import cf.wynntils.modules.core.overlays.UpdateOverlay;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@ModuleInfo(name = "core", displayName = "Core")
public class CoreModule extends Module {

    private static CoreModule module;

    public void onEnable() {
        registerEvents(new ServerEvents());

        registerOverlay(new UpdateOverlay(), Priority.HIGHEST);
        registerOverlay(new DownloadOverlay(), Priority.HIGHEST);

        registerSettings(CoreDBConfig.class);

        module = this;
    }

    public static CoreModule getModule() {
        return module;
    }

}
