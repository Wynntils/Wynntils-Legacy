/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

public class SchedulerEvent extends Event {

    /**
     * Called every 3 seconds when the client tries to update the character location
     */
    public static class RegionUpdate extends SchedulerEvent {

    }

}
