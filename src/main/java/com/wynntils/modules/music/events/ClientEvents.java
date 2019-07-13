/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.music.events;

import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.events.custom.WynnTerritoryChangeEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.music.managers.MusicManager;
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
        if(e.phase == TickEvent.Phase.START) return;

        MusicManager.getPlayer().setupController();
    }

    @SubscribeEvent
    public void serverLeft(WynncraftServerEvent.Leave e) {
        MusicManager.getPlayer().stop();
    }

}
