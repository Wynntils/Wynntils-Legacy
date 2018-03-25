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
@SettingsInfo(name = "main")
public class RichPresenceConfig extends SettingsHolder {

    @Setting(displayName = "Entering Notifier", description = "Do you want to receive notification of area entering?")
    public boolean enteringNotifier = true;

    @Setting(displayName = "User Information", description = "Do you want to allow to show your nick and class at RichPresence?")
    public boolean showUserInformation = true;

    @Override
    public void onSettingChanged(String name) {

    }

}
