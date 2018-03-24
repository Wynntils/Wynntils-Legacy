package cf.wynntils.modules.example;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */

/**
 * @JsonIgnoreProperties is needed 100% needed, you always will need to put it
 * @SettingsInfo is just the name of the config, can more things be added later
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@SettingsInfo(name = "ExampleConfig")
public class ExampleSettings extends SettingsHolder {

    /**
     * Normal config with the @Setting annotation
     */
    @Setting(displayName = "my display name", description = "my description")
    public boolean myField = true;

    /**
     * When a config setting is changed
     *
     * @param name fieldName
     */
    @Override
    public void onSettingChanged(String name) {
        if(name.equals("myField")) {
            //do something
        }
    }

    /**
     * If you want to set a config option hardcoded like
     * myField = true;
     * don't forget to call after saveSettings(Module M)
     *
     * Example:
     *
     * public void changeMyConfigValue() {
     *     myField = true;
     *     saveSettings(ExampleModule.getModule());
     * }
     *
     * Easy, isn't?
     */

}
