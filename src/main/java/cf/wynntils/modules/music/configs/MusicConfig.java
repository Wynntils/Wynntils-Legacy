/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name = "questbook", displayPath = "QuestBook")
public class MusicConfig extends SettingsClass {

    public static MusicConfig INSTANCE;

    @Setting(displayName = "Sound System", description = "Should the Wynncraft Sound system be replaced with Wynntils Sound System")
    public boolean allowMusicModule = false;


}
