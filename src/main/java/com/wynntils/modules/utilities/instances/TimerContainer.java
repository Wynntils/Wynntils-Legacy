/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.instances;

public abstract class TimerContainer {

    private final String name; // The name of the consumable (also used to identify it)
    private final boolean persistent; // If the consumable is should persist through death and character changes

    public TimerContainer(String name, boolean persistent) {
        this.name = name;
        this.persistent = persistent;
    }

    /**
     * @return The name of the consumable
     */
    public String getName() {
        return name;
    }

    /**
     * @return If the consumable should persist through death and character changes
     */
    public boolean isPersistent() {
        return persistent;
    }
}
