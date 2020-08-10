/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.music.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.events.custom.WynnTerritoryChangeEvent;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.music.managers.MusicManager;
import com.wynntils.webapi.WebManager;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class ClientEvents implements Listener {

    @SubscribeEvent
    public void onTerritoryUpdate(WynnTerritoryChangeEvent e) {
        if (e.getNewTerritory().equals("")) return;

        MusicManager.checkForMusic(e.getNewTerritory());
    }

    @SubscribeEvent
    public void classChange(WynnClassChangeEvent e) {
        if (e.getNewClass() != ClassType.NONE || Reference.onWorld) return;

        MusicManager.getPlayer().stop();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void openClassSelection(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!e.getGui().getLowerInv().getName().contains("Select a Class")) return;

        MusicManager.playSong(WebManager.getApiUrl("CharacterSelectionSong"), true);
        MusicManager.setFastSwitchNext();
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) return;

        MusicManager.getPlayer().updatePlayer();
    }

    @SubscribeEvent
    public void serverLeft(WynncraftServerEvent.Leave e) {
        MusicManager.getPlayer().stop();
    }

}
