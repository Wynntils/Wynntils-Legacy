/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Called when the player joins a world on the Wynncraft Server
 *
 */
public class WynnWorldJoinEvent extends Event {

    String world;

    public WynnWorldJoinEvent(String world) {
        this.world = world;
    }

    public String getWorld() {
        return world;
    }

}
