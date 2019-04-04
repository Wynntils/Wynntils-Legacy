/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.richpresence.events.ServerEvents;

@SettingsInfo(name = "main", displayPath = "wynntils.config.richpresence.display_path")
public class RichPresenceConfig extends SettingsClass {
    public static RichPresenceConfig INSTANCE;

    @Setting(displayName = "wynntils.config.richpresence.show_information.display_name", description = "wynntils.config.richpresence.show_information.description")
    public boolean showUserInformation = true;
    
    @Setting(displayName = "wynntils.config.richpresence.enable.display_name", description = "wynntils.config.richpresence.enable.description")
    public boolean enableRichPresence = true;

    @Override
    public void onSettingChanged(String name) {
        if (name.equals("enableRichPresence")) ServerEvents.onEnableSettingChange();
    }

}
