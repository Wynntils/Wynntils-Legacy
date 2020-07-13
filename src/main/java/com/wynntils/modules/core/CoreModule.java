/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.core;

import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.core.commands.*;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.events.ClientEvents;
import com.wynntils.modules.core.events.ServerEvents;
import com.wynntils.modules.core.managers.TabManager;
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

        registerCommand(new CommandAdmin());
        registerCommand(new CommandCompass());
        registerCommand(new CommandForceUpdate());
        registerCommand(new CommandServer());
        registerCommand(new CommandTerritory());
        registerCommand(new CommandToken());
        registerCommand(new CommandWynntils());
        registerCommand(new CommandTest());

        TabManager.replaceTabOrderer();

        module = this;
    }

    public static CoreModule getModule() {
        return module;
    }


}
