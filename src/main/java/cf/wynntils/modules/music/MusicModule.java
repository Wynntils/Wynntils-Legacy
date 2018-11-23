/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.music.configs.MusicConfig;
import cf.wynntils.modules.music.events.ClientEvents;
import cf.wynntils.modules.music.managers.MusicManager;
import cf.wynntils.modules.music.overlays.inventories.CurrentMusicDisplayer;

@ModuleInfo(name = "sounds", displayName = "WynnSounds")
public class MusicModule extends Module {

    @Override
    public void onEnable() {
        registerSettings(MusicConfig.class);
        registerEvents(new ClientEvents());
        registerEvents(new CurrentMusicDisplayer());

        MusicManager.checkForUpdates();
    }

}
