/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.richpresence.profiles;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.WynnTerritoryChangeEvent;
import com.wynntils.core.framework.FrameworkManager;

public class DataProfile {

    String location = "Waiting";
    boolean unknownLocation = false;

    public DataProfile() {
    }

    public String getLocation() {
        return location;
    }

    public boolean getUnknownLocation() {
        return unknownLocation;
    }

    public void setLocation(String value) {
        ModCore.mc().addScheduledTask(() -> {
            FrameworkManager.getEventBus().post(new WynnTerritoryChangeEvent(location, value));
        });
        location = value;
    }

    public void setUnknownLocation(boolean value) {
        unknownLocation = value;
    }

}
