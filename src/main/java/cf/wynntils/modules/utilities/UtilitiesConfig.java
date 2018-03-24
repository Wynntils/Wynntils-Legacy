package cf.wynntils.modules.utilities;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@SettingsInfo(name = "main")
public class UtilitiesConfig extends SettingsHolder {

    @Setting(displayName = "imATest", description = "another Test")
    public String teststst = "teststs";

    @Override
    public void onSettingChanged(String name) {

    }

}
