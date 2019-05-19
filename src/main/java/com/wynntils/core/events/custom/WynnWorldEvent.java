/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

public class WynnWorldEvent extends Event {

    /**
     * Triggered when the user joins a loads world
     * that means the user is already inside the game and can play
     */
    public static class Join extends WynnWorldEvent {

        String world;

        public Join(String world) {
            this.world = world;
        }

        public String getWorld() {
            return world;
        }

    }

    /**
     * Triggered when the user leaves a world
     * this does not means that the user is already on lobby
     */
    public static class Leave extends WynnWorldEvent {}

}
