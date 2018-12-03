package cf.wynntils.modules.richpresence;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.richpresence.events.ChatEvents;
import cf.wynntils.modules.richpresence.events.ServerEvents;
import cf.wynntils.modules.richpresence.overlays.LocationOverlay;
import cf.wynntils.modules.richpresence.profiles.DataProfile;
import cf.wynntils.modules.richpresence.profiles.RichProfile;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@ModuleInfo(name = "rich_presence", displayName = "Rich Presence")
public class RichPresenceModule extends Module {

    private static RichPresenceModule module;

    private RichProfile richPresence;
    private DataProfile modData = new DataProfile();

    public void onEnable() {
        try {
            richPresence = new RichProfile("387266678607577088");
        } catch (Exception e) {
            e.printStackTrace();
        }

        module = this;

        registerOverlay(new LocationOverlay(), Priority.NORMAL);
        registerEvents(new ChatEvents());
        registerEvents(new ServerEvents());

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

    /**
     * Get the current session saved data
     * @return Memory data profile
     */
    public DataProfile getData() {
        return modData;
    }
}
