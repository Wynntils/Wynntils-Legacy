/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.example;

import com.wynntils.core.framework.interfaces.Listener;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** EXAMPLE CLASS
 * ExampleListener shows some of the things that are needed to make
 * an event listener class.
 * An event listener class is a class that will automatically run
 * tagged methods upon their Event association in the params getting
 * thrown.
 */
public class ExampleListener implements Listener {

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority HIGHEST
     *
     * @param e Requested Event
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void tickEventHH(TickEvent.ClientTickEvent e) {
    }

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority HIGH
     *
     * @param e Requested Event
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void tickEventH(TickEvent.ClientTickEvent e) {
    }

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority NORMAL // default
     *
     * @param e Requested Event
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void tickEventN(TickEvent.ClientTickEvent e) {
    }

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority LOW
     *
     * @param e Requested Event
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void tickEventL(TickEvent.ClientTickEvent e) {
    }

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority LOWEST
     *
     * @param e Requested Event
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tickEventLL(TickEvent.ClientTickEvent e) {
    }

}
