/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.music.configs;

import com.wynntils.Reference;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.music.managers.MusicManager;
import com.wynntils.modules.richpresence.RichPresenceModule;

@SettingsInfo(name = "music", displayPath = "wynntils.config.music.display_path")
public class MusicConfig extends SettingsClass {

    public static MusicConfig INSTANCE;

    @Setting(displayName = "wynntils.config.music.allow_music.display_name", description = "wynntils.config.music.allow_music.description", order = 0)
    public boolean allowMusicModule = false;

    @Setting(displayName = "wynntils.config.music.volume.display_name", description = "wynntils.config.music.volume.description")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float baseVolume = 1;

    @Setting(displayName = "wynntils.config.music.background_volume.display_name", description = "wynntils.config.music.background_volume.description")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float focusVolume = -10;

    @Override
    public void onSettingChanged(String name) {
        if(!allowMusicModule && Reference.onWorld) MusicManager.getPlayer().stop();
        if(allowMusicModule && Reference.onWorld) MusicManager.checkForMusic(RichPresenceModule.getModule().getData().getLocation());
    }

}
