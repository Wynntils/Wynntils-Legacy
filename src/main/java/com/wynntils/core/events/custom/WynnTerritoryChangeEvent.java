/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

public class WynnTerritoryChangeEvent extends Event {

    String oldTerritory, newTerritory;

    public WynnTerritoryChangeEvent(String oldTerritory, String newTerritory) {
        this.oldTerritory = oldTerritory; this.newTerritory = newTerritory;
    }

    public String getNewTerritory() {
        return newTerritory;
    }

    public String getOldTerritory() {
        return oldTerritory;
    }

}
