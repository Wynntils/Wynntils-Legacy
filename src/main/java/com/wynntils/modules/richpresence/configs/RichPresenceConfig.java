/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.richpresence.events.ServerEvents;

@SettingsInfo(name = "main", displayPath = "Main")
public class RichPresenceConfig extends SettingsClass {
    public static RichPresenceConfig INSTANCE;

    @Setting(displayName = "Show Class Info", description = "Should Rich Presence display basic information about the class you're using?")
    public boolean showUserInformation = true;
    
    @Setting(displayName = "Enable Rich Presence", description = "Should the mod enable Rich Presence on Discord?")
    public boolean enableRichPresence = true;

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("enableRichPresence")) ServerEvents.onEnableSettingChange();
    }

}
