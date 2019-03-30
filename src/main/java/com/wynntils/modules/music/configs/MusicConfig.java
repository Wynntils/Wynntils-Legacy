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

@SettingsInfo(name = "music", displayPath = "Music")
public class MusicConfig extends SettingsClass {

    public static MusicConfig INSTANCE;

    @Setting(displayName = "Music System", description = "Should Wynncraft's music system be replaced by Wynntils'?", order = 0)
    public boolean allowMusicModule = false;

    @Setting(displayName = "Base Volume", description = "How loud should all soundtracks be?")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float baseVolume = 1;

    @Setting(displayName = "Off Focus Volume", description = "How loud should the soundtrack be when Minecraft is not focused on?")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float focusVolume = -10;

    @Override
    public void onSettingChanged(String name) {
        if(!allowMusicModule && Reference.onWorld) MusicManager.getPlayer().stop();
        if(allowMusicModule && Reference.onWorld) MusicManager.checkForMusic(RichPresenceModule.getModule().getData().getLocation());
    }

}
