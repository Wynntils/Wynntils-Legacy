/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.example;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

/**
 * @JsonIgnoreProperties is needed 100% needed, you always will need to put it
 * @SettingsInfo is just the name of the config, can more things be added later
 */
@SettingsInfo(name = "example_settings", displayPath = "Example Settings")
public class ExampleSettings extends SettingsClass {
    /** You must ALWAYS put an INSTANCE
     *  of the class!
     */
    public static ExampleSettings INSTANCE;


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
