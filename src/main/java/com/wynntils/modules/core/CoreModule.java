/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core;

import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.events.ClientEvents;
import com.wynntils.modules.core.events.ServerEvents;
import com.wynntils.modules.core.overlays.DownloadOverlay;
import com.wynntils.modules.core.overlays.UpdateOverlay;

@ModuleInfo(name = "core", displayName = "Core")
public class CoreModule extends Module {

    private static CoreModule module;

    public void onEnable() {
        registerEvents(new ClientEvents());
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
