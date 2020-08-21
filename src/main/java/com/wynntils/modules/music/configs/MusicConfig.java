/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.music.configs;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.music.managers.SoundTrackManager;

@SettingsInfo(name = "music", displayPath = "Music")
public class MusicConfig extends SettingsClass {

    public static MusicConfig INSTANCE;

    @Setting(displayName = "Music System", description = "Should Wynntils Music Player be enabled", order = 0)
    public boolean enabled = true;

    @Setting(displayName = "Replace Wynncraft Jukebox", description = "Should the Wynncraft Jukebox be replaced with an offline version\nThis is a useful solution if you're lagged but\nit's not precise as Wynncraft (86.7% precision).\n\nType /toggle music to avoid duplicates.", order = 1)
    public boolean replaceJukebox = false;

    @Setting(displayName = "Class Selection Music", description = "Should the Class Selection Music be played", order = 2)
    public boolean classSelectionMusic = true;

    @Setting(displayName = "Base Volume", description = "How loud should all soundtracks be?")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float baseVolume = -10;

    @Setting(displayName = "Off Focus Volume", description = "How loud should the soundtrack be when Minecraft is not focused on?")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float focusVolume = -15;

    @Setting(displayName = "Transition Jump", description = "How fast should the song transition be?")
    @Setting.Limitations.FloatLimit(max = 2f, min= 0f)
    public float switchJump = 0.5f;

    @Override
    public void onSettingChanged(String name) {
        if (!enabled && Reference.onWorld) SoundTrackManager.getPlayer().stop();
        if (!replaceJukebox && PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE) SoundTrackManager.getPlayer().stop();
    }

}
