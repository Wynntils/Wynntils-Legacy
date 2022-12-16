/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.instances;

public class StaticTimerContainer extends TimerContainer {

    private String displayedTime; // The displayed time remaining. Allows for xx:xx for infinite time effects.

    public StaticTimerContainer(String prefix, String name, String suffix, String displayedTime, boolean persistent) {
        super(prefix, name, suffix, persistent);
        this.displayedTime = displayedTime;
    }

    /**
     * @return The time remaining for the consumable
     */
    public String getDisplayedTime() {
        return displayedTime;
    }

    /**
     * @param displayedTime The new time remaining for the consumable
     */
    public void setDisplayedTime(String displayedTime) {
        this.displayedTime = displayedTime;
    }
}
