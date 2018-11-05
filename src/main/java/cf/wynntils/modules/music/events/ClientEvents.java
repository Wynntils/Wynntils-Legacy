/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music.events;

import cf.wynntils.core.events.custom.WynnClassChangeEvent;
import cf.wynntils.core.events.custom.WynnTerritoryChangeEvent;
import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.modules.music.managers.MusicManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class ClientEvents implements Listener {

    @SubscribeEvent
    public void onTerritoryUpdate(WynnTerritoryChangeEvent e) {
       if(e.getNewTerritory().equals("Waiting")) return;

       MusicManager.checkForMusic(e.getNewTerritory());
    }

    @SubscribeEvent
    public void classChange(WynnClassChangeEvent e) {
        if(e.getCurrentClass() == ClassType.NONE) MusicManager.getPlayer().stop();
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        MusicManager.getPlayer().setupController();
    }

    @SubscribeEvent
    public void serverLeft(WynncraftServerEvent.Leave e) {
        MusicManager.getPlayer().stop();
    }

}
