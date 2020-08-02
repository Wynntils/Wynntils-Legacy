/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.enums.ClassType;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Triggered when the player changes a class inside the Wynncraft Server
 *
 */
public class WynnClassChangeEvent extends Event {

    private final ClassType newClass;

    private final boolean newClassIsReskinned;

    public WynnClassChangeEvent(ClassType newClass, boolean newClassIsReskinned) {
        this.newClass = newClass;
        this.newClassIsReskinned = newClassIsReskinned;
    }

    public ClassType getNewClass() {
        return newClass;
    }

    public boolean isNewClassIsReskinned() {
        return newClassIsReskinned;
    }

}
