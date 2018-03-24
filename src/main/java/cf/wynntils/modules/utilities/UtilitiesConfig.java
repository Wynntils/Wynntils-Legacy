package cf.wynntils.modules.utilities;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;


/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */

@SettingsInfo(name = "main")
public class UtilitiesConfig extends SettingsHolder {

    @Setting(displayName = "imATest", description = "another Test")
    public boolean isActive = true;

    @Override
    public void onSettingChanged(String name) {

    }

}
