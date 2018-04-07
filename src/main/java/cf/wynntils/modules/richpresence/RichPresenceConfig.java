package cf.wynntils.modules.richpresence;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@SettingsInfo(name = "main", displayPath = "Main")
public class RichPresenceConfig extends SettingsHolder {
    public static RichPresenceConfig INSTANCE;


    @Setting(displayName = "Entering Notification", description = "Show a notification in the upper corner when entering a region")
    public boolean enteringNotifier = true;

    @Setting(displayName = "Show class info", description = "Should RichPresence show in discord basic information about the current class")
    public boolean showUserInformation = true;

    @Override
    public void onSettingChanged(String name) {

    }

}
