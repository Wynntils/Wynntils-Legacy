/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.core.config;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@SettingsInfo(name = "data", ignore = true)
public class CoreDBConfig extends SettingsHolder {

    @Setting(displayName = "useless", description = "useless")
    public String lastToken = "";

    @Override
    public void onSettingChanged(String name) {

    }

}
