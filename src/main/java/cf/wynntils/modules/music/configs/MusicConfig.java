/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music.configs;

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

    @Override
    public void onSettingChanged(String name) {
        if(!allowMusicModule) MusicManager.getPlayer().stop();
        if(allowMusicModule) MusicManager.checkForMusic(RichPresenceModule.getModule().getData().getLocation());
    }

}
