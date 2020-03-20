/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.configs;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "sounds", displayPath = "Utilities/Sound Effects")
public class SoundEffectsConfig extends SettingsClass {
    public static SoundEffectsConfig INSTANCE;

    @Setting(displayName = "Horse Whistle", description = "Should the horse whistle be played?")
    public boolean horseWhistle = true;

    @Setting(displayName = "War Horn", description = "Should the warn horn be played?")
    public boolean warHorn = true;

    @Setting(displayName = "Mythic Found", description = "Should the mythic found sound be played?")
    public boolean mythicFound = true;

}
