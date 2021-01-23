/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.instances.data;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.WynnTerritoryChangeEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.instances.containers.PlayerData;

public class LocationData extends PlayerData {

    String location = "";

    public LocationData() { }

    /**
     * @return The territory the player is in, empty if the player is not in map
     */
    public String getLocation() {
        return location;
    }

    /**
     * Updates the player location
     * @param location
     */
    public void setLocation(String location) {
        ModCore.mc().addScheduledTask(() -> {
            FrameworkManager.getEventBus().post(new WynnTerritoryChangeEvent(this.location, location));
        });

        this.location = location;
    }

    /**
     * @return If the player is outside of a territory
     */
    public boolean isInUnknownLocation() {
        return location.isEmpty();
    }

}
