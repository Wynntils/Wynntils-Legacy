/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.events.custom;

import com.wynntils.core.framework.enums.ClassType;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Triggered when the player changes a class inside the Wynncraft Server
 *
 */
public class WynnClassChangeEvent extends Event {

    ClassType oldClass;
    ClassType currentClass;


    public WynnClassChangeEvent(ClassType oldClass, ClassType currentClass) {
        this.oldClass = oldClass; this.currentClass = currentClass;
    }

    public ClassType getOldClass() {
        return oldClass;
    }

    public ClassType getCurrentClass() {
        return currentClass;
    }

}
