/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.music.configs;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.music.managers.MusicManager;

@SettingsInfo(name = "music", displayPath = "Music")
public class MusicConfig extends SettingsClass {

    public static MusicConfig INSTANCE;

    @Setting(displayName = "Music System", description = "Should Wynntils Music Player be enabled", order = 0)
    public boolean enabled = true;

    @Setting(displayName = "Replace Wynncraft Jukebox", description = "Should the Wynncraft Jukebox be replaced with an offline version\nThis is a useful solution if you're lagged but\nit's not precise as Wynncraft (86.7% precision).\n\nType /toggle music to avoid duplicates.", order = 0)
    public boolean replaceJukebox = false;

    @Setting(displayName = "Base Volume", description = "How loud should all soundtracks be?")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float baseVolume = 1;

    @Setting(displayName = "Off Focus Volume", description = "How loud should the soundtrack be when Minecraft is not focused on?")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float focusVolume = -10;

    @Override
    public void onSettingChanged(String name) {
        if (!enabled && Reference.onWorld) MusicManager.getPlayer().stop();
        if (replaceJukebox && Reference.onWorld) MusicManager.checkForMusic(PlayerInfo.getPlayerInfo().getLocation());
    }

}
