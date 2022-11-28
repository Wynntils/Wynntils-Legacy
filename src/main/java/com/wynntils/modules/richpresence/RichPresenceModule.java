/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.richpresence.configs.RichPresenceConfig;
import com.wynntils.modules.richpresence.events.ClientEvents;
import com.wynntils.modules.richpresence.events.ServerEvents;
import com.wynntils.modules.richpresence.profiles.CoreWrapper;

@ModuleInfo(name = "rich_presence", displayName = "Rich Presence")
public class RichPresenceModule extends Module {

    private static RichPresenceModule module;

    private CoreWrapper coreWrapper;

    public void onEnable() {
        try {
            coreWrapper = new CoreWrapper(387266678607577088L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        module = this;

        registerEvents(new ServerEvents());
        registerEvents(new ClientEvents());

        registerSettings(RichPresenceConfig.class);
    }

    public static RichPresenceModule getModule() {
        return module;
    }

    public CoreWrapper getCoreWrapper() {
        return coreWrapper;
    }

}
