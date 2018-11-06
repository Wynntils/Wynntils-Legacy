/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music.configs;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;
import cf.wynntils.modules.music.managers.MusicManager;
import cf.wynntils.modules.richpresence.RichPresenceModule;

@SettingsInfo(name = "music", displayPath = "Music")
public class MusicConfig extends SettingsClass {

    public static MusicConfig INSTANCE;

    @Setting(displayName = "Music System", description = "Should the Wynncraft Music System be replaced with Wynntils Music System")
    public boolean allowMusicModule = false;

    @Setting(displayName = "Base Volume", description = "The music volume")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float baseVolume = 1;

    @Setting(displayName = "Off Focus Volume", description = "The volume that will be if the game is out of focus")
    @Setting.Limitations.FloatLimit(max = 1f, min= -50f, precision = 1f)
    public float focusVolume = -10;

    @Override
    public void onSettingChanged(String name) {
        if(!allowMusicModule && Reference.onWorld) MusicManager.getPlayer().stop();
        if(allowMusicModule && Reference.onWorld) MusicManager.checkForMusic(RichPresenceModule.getModule().getData().getLocation());
    }

}
