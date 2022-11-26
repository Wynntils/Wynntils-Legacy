/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.richpresence;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.richpresence.configs.RichPresenceConfig;
import com.wynntils.modules.richpresence.events.ClientEvents;
import com.wynntils.modules.richpresence.events.ServerEvents;
import com.wynntils.modules.richpresence.profiles.RichProfile;

@ModuleInfo(name = "rich_presence", displayName = "Rich Presence")
public class RichPresenceModule extends Module {

    private static RichPresenceModule module;

    private RichProfile richPresence;

    public void onEnable() {
        try {
            richPresence = new RichProfile(387266678607577088L);
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

    /**
     * Get the current RichPresence online instance
     * @return RichPresence profile
     */
    public RichProfile getRichPresence() {
        return richPresence;
    }

}
