/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music.events;

import cf.wynntils.core.events.custom.WynnClassChangeEvent;
import cf.wynntils.core.events.custom.WynnTerritoryChangeEvent;
import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.music.managers.MusicManager;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class ClientEvents implements Listener {

    @EventHandler
    public void onTerritoryUpdate(WynnTerritoryChangeEvent e) {
       if(e.getNewTerritory().equals("Waiting")) return;

       MusicManager.checkForMusic(e.getNewTerritory());
    }

    @EventHandler
    public void classChange(WynnClassChangeEvent e) {
        if(e.getCurrentClass() == ClassType.NONE) MusicManager.getPlayer().stop();
    }

    @EventHandler
    public void tick(TickEvent.ClientTickEvent e) {
        MusicManager.getPlayer().setupController();
    }

    @EventHandler
    public void serverLeft(WynncraftServerEvent.Leave e) {
        MusicManager.getPlayer().stop();
    }

}
