/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "sounds", displayPath = "Utilities/Sounds")
public class SoundsConfig extends SettingsClass {
    public static SoundsConfig INSTANCE;

    @Setting(displayName = "Horse Whistle", description = "Should the horse whistle be played?")
    public boolean horseWhistle = true;

    @Setting(displayName = "War Horn", description = "Should the warn horn be played?")
    public boolean warHorn = true;

}
