/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.instances;

public class StaticTimerContainer extends TimerContainer {

    private String displayedTime; // The displayed time remaining. Allows for xx:xx for infinite time effects.
    private String prefix; // The prefix to display before the name. Not included in identifying name.

    public StaticTimerContainer(String prefix, String name, String displayedTime, boolean persistent) {
        super(name, persistent);
        this.prefix = prefix;
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

    /**
     * @return The prefix to display before the name
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix The new prefix to display before the name
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
