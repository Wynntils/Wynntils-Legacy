/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.music;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.events.ClientEvents;
import com.wynntils.modules.music.managers.SoundTrackManager;
import com.wynntils.modules.music.overlays.inventories.CurrentMusicDisplayer;

@ModuleInfo(name = "sounds", displayName = "WynnSounds")
public class MusicModule extends Module {

    @Override
    public void onEnable() {
        registerSettings(MusicConfig.class);
        registerEvents(new ClientEvents());
        registerEvents(new CurrentMusicDisplayer());

        SoundTrackManager.updateSongList();
    }

}
