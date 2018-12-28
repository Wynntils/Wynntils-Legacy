package cf.wynntils.modules.richpresence;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;
import cf.wynntils.modules.richpresence.events.ServerEvents;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
@SettingsInfo(name = "main", displayPath = "Main")
public class RichPresenceConfig extends SettingsClass {
    public static RichPresenceConfig INSTANCE;


    @Setting(displayName = "Entering Notification", description = "Should a notification be displayed in the upper-right corner when entering a region?")
    public boolean enteringNotifier = true;

    @Setting(displayName = "Show Class Info", description = "Should Rich Presence display basic information about the class you're using?")
    public boolean showUserInformation = true;
    
    @Setting(displayName = "Enable Rich Presence", description = "Should the mod enable Rich Presence on Discord?")
    public boolean enableRichPresence = true;

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("enableRichPresence")) {
            ServerEvents.onEnableSettingChange();
        }
    }

}
