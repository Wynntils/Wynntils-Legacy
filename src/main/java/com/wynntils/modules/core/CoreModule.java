/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core;

import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.KeyHolder;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.events.ClientEvents;
import com.wynntils.modules.core.events.ServerEvents;
import com.wynntils.modules.core.overlays.DownloadOverlay;
import com.wynntils.modules.core.overlays.UpdateOverlay;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "core", displayName = "Core")
public class CoreModule extends Module {

    private static CoreModule module;

    public void onEnable() {
        registerEvents(new ClientEvents());
        registerEvents(new ServerEvents());

        KeyHolder accept = registerKeyBinding("Accept Update Start", Keyboard.KEY_Y, "Wynntils", true, () -> {
            UpdateOverlay.triggerStartUpdate();
        });
        KeyHolder cancel = registerKeyBinding("Cancel Update Start", Keyboard.KEY_N, "Wynntils", true, () -> {
            UpdateOverlay.triggerCancelUpdate();
        });

        registerOverlay(new UpdateOverlay(accept, cancel), Priority.HIGHEST);
        registerOverlay(new DownloadOverlay(), Priority.HIGHEST);

        registerSettings(CoreDBConfig.class);

        module = this;
    }

    public static CoreModule getModule() {
        return module;
    }


}
