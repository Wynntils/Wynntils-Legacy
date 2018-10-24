/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music.events;

import cf.wynntils.core.events.custom.WynnTerritoryChangeEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.music.managers.MusicManager;


public class ClientEvents implements Listener {

    @EventHandler
    public void onTerritoryUpdate(WynnTerritoryChangeEvent e) {
        MusicManager.checkForMusic();
    }

}
