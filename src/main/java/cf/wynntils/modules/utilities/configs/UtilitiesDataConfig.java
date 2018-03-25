package cf.wynntils.modules.utilities.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by HeyZeer0 on 25/03/2018.
 * Copyright © HeyZeer0 - 2016
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@SettingsInfo(name = "data", ignore = true)
public class UtilitiesDataConfig extends SettingsHolder {

    @Setting(displayName = "useless", description = "useless")
    public long dailyReminder = 0L;

    @Override
    public void onSettingChanged(String name) {

    }

}
