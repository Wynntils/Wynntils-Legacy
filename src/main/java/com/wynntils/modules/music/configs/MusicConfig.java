/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.music.configs;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;
import com.wynntils.modules.core.enums.ToggleSetting;
import com.wynntils.modules.music.managers.SoundTrackManager;

@SettingsInfo(name = "music", displayPath = "Music")
public class MusicConfig extends SettingsClass {
    public static MusicConfig INSTANCE;

    @Setting(displayName = "Music System", description = "Should Wynntils' music player be enabled?", order = 0)
    public boolean enabled = true;

    @Setting(displayName = "Replace Wynncraft Jukebox", description = "Should Wynncraft's jukebox be replaced with an offline version?\n\n§8This is a useful feature if you usually lag but it's not as precise as Wynncraft's music player with 86.7% precision.\n\n§8Type `/toggle music` to avoid duplicate music being played.", order = 1)
    public boolean replaceJukebox = false;

    @Setting(displayName = "Class Selection Music", description = "Should the character selection music be played?", order = 2)
    public boolean classSelectionMusic = true;

    @Setting(displayName = "Quieter Character Selector", description = "Should Character selector music be played more quietly?", order = 3)
    public boolean characterSelectorQuiet = true;

    @Setting(displayName = "Off Focus Volume Offset", description = "How loud should soundtracks be when Minecraft is not focused on?")
    @Setting.Limitations.FloatLimit(max = 0f, min= -32f, precision = 1f)
    public float focusOffset = -10;

    @Setting(displayName = "Transition Jump", description = "How long should song transitions be?")
    @Setting.Limitations.FloatLimit(max = 2f, min= 0f)
    public float switchJump = 0.5f;

    @SettingsInfo(name = "soundeffects", displayPath = "Music/Sound Effects")
    public static class SoundEffects extends SettingsClass {
        public static SoundEffects INSTANCE;

        @Setting(displayName = "Mythic Found", description = "Should a sound be played when a mythic is found in a loot chest?")
        public boolean mythicFound = true;

    }

    @Override
    public void onSettingChanged(String name) {
        if (!enabled && Reference.onWorld) SoundTrackManager.getPlayer().stop();
        if (!replaceJukebox) SoundTrackManager.getPlayer().stop();
        else if (Reference.onWorld && PlayerInfo.get(CharacterData.class).isLoaded()) {
            ToggleSetting.MUSIC.set(false);
        }
    }

}
