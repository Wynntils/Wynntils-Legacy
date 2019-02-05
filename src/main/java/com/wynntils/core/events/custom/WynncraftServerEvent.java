/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Represents events that are called when the player join or leave the Wynncraft Server
 *
 */
public class WynncraftServerEvent extends Event {

    /**
     * Called when the player login into the Wynncraft Server
     *
     */
    public static class Login extends WynncraftServerEvent { }

    /**
     * Called when the player leaves the Wynncraft Server
     *
     */
    public static class Leave extends WynncraftServerEvent { }


}
