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

    @Setting(displayName = "Music System", description = "Should Wynntils' music player be enabled?", order = 0)
    public boolean enabled = true;

    @Setting(displayName = "Replace Wynncraft Jukebox", description = "Should Wynncraft's jukebox be replaced with an offline version?\n\n§8This is a useful feature if you usually lag but it's not as precise as Wynncraft's music player with 86.7% precision.\n\n§8Type `/toggle music` to avoid duplicate music being played.", order = 1)
    public boolean replaceJukebox = false;

    @Setting(displayName = "Class Selection Music", description = "Should the class selection music be played?", order = 2)
    public boolean classSelectionMusic = true;

    @Setting(displayName = "Base Volume", description = "How loud should soundtracks be?")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float baseVolume = -10;

    @Setting(displayName = "Off Focus Volume", description = "How loud should soundtracks be when Minecraft is not focused on?")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float focusVolume = -15;

    @Setting(displayName = "Transition Jump", description = "How long should song transitions be?")
    @Setting.Limitations.FloatLimit(max = 2f, min= 0f)
    public float switchJump = 0.5f;

    @Override
    public void onSettingChanged(String name) {
        if (!enabled && Reference.onWorld) SoundTrackManager.getPlayer().stop();
        if (!replaceJukebox && PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE) SoundTrackManager.getPlayer().stop();
    }

}
