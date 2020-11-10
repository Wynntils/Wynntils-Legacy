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

    @Setting(displayName = "Horse Whistle", description = "Should a horse whistle be played when you summon your horse?")
    public boolean horseWhistle = true;

    @Setting(displayName = "War Horn", description = "Should a war horn be played when your guild attacks a territory?")
    public boolean warHorn = true;

    @Setting(displayName = "Mythic Found", description = "Should a sound be played when a mythic is found in a loot chest?")
    public boolean mythicFound = true;

}
