package cf.wynntils.modules.core;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.core.config.CoreDBConfig;
import cf.wynntils.modules.core.overlays.DownloadOverlay;
import cf.wynntils.modules.core.overlays.UpdateOverlay;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@ModuleInfo(name = "Core")
public class CoreModule extends Module {

    private static CoreDBConfig database;
    private static CoreModule module;

    public void onEnable() {
        registerOverlay(new UpdateOverlay(), Priority.HIGHEST);
        registerOverlay(new DownloadOverlay(), Priority.HIGHEST);

        database = new CoreDBConfig();
        registerSettings(database);

        module = this;
    }

    public static CoreDBConfig getDatabase() {
        return database;
    }

    public static CoreModule getModule() {
        return module;
    }

}
